import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import java.util.zip.ZipOutputStream
import java.util.zip.ZipEntry
import org.plweb.suite.common.xml.XmlFactory
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response, session)

def sql = new Sql(helper.connection)

if (!session) {
	response.sendError 403
	return
}

def class_id = helper.fetch('id')
def lang_type = helper.fetch('lang')

if (class_id) {

	//response.contentType = 'text/plain'
	response.contentType = "application/zip"
	response.setHeader("Content-disposition", "attachment; filename=${class_id}.zip")

	def zout  = new ZipOutputStream(response.outputStream)

	def classRow = sql.firstRow("select * from ST_CLASS where CLASS_ID=?", [class_id])

	def classTitle = classRow.CLASS_NAME

	def classUsers = []

	sql.eachRow("select b.USER_ID, b.NAME, b.ENROLLMENT from USER_CLASS a left outer join ST_USER b on a.USER_ID=b.USER_ID where CLASS_ID=? and IS_TEACHER='n'", [class_id]) {
		classUsers << [it.USER_ID, it.NAME, it.ENROLLMENT]
	}

	def chapterNo = 0

	sql.eachRow("select * from CLASS_COURSE where CLASS_ID=? order by SEQNUM", [class_id]) {
		classCourse ->

		chapterNo++

		def buffer = new StringBuffer()

		def chapterTitle = classCourse.TITLE

		buffer << '*'.multiply(chapterTitle.bytes.size()) << "\n"
		buffer << chapterTitle << "\n"
		buffer << '*'.multiply(chapterTitle.bytes.size()) << "\n"
		buffer << "\n"

		classUsers.each {
			user ->

			def userTitle = "${user[1]} ${user[2]}".trim()
			buffer << userTitle << "\n"
			buffer << '='.multiply(userTitle.bytes.size()) << "\n"
			buffer << "\n"

			def qno = 0
			sql.eachRow("select TYPE, MESSAGE, CODE from ST_REPORTS where CLASS_ID=? and COURSE_ID=? and LESSON_ID=? and USER_ID=? order by QUESTION_ID", [class_id, classCourse.COURSE_ID, classCourse.LESSON_ID, user[0]]) {
				report ->

				qno++

				def qtitle = "第 ${qno} 題"
				buffer << qtitle << "\n"
				buffer << '-'.multiply(qtitle?.toString().bytes.size()) << "\n"
				buffer << "\n"

				buffer << "測驗結果："
				switch (report.TYPE) {
					case 'test_ok':
						buffer << '通過' << "\n"
					break;
					default:
						buffer << '錯誤' << "\n"
				}
				buffer << "\n"

				buffer << '程式碼：' << "\n"
				buffer << "\n"

				buffer << ".. code-block:: ${lang_type}" << "\n"
				buffer << "\n"
				buffer << "\t# src/${user[2]}/chapter${chapterNo}/ex${qno}" << "\n"
				report.CODE?.replace("\r", '').split("\n").each {
					line ->
					buffer << "\t${line}" << "\n"
				}
				buffer << "\n"

				buffer << "輸出訊息： ::" << "\n"
				buffer << "\n"
				buffer << "\t# src/${user[2]}/chapter${chapterNo}/ex${qno}" << "\n"
				report.MESSAGE?.replace("\r", '').split("\n").each {
					line ->
					buffer << "\t${line}" << "\n"
				}
				buffer << "\n"

				zout.putNextEntry(new ZipEntry("src/${user[2]}/chapter${chapterNo}/ex${qno}.txt"))
				zout.write(report.CODE?.bytes)
			}
		}

		zout.putNextEntry(new ZipEntry("book/chapter${chapterNo}.rst"))
		zout.write(buffer?.toString().bytes)
	}

	def indexBuffer = new StringBuffer()
	indexBuffer << """.. report
   @project: class${class_id}
   @title: ${classTitle}
   @copyright: PLWeb
   @authors: AACSB學習成果報告
   @language: zh_TW
   @latex_paper_size: a4
   @latex_font_size: 11pt
   @epub_theme: epub_simple

${'#'.multiply(classTitle.bytes.size())}
${classTitle}
${'#'.multiply(classTitle.bytes.size())}

.. toctree::
   :maxdepth: 1

"""

	def i
	for (i = 1; i <= chapterNo; i++) {
		indexBuffer << "   chapter${i}" << "\n"
	}

	indexBuffer << "\n"

	zout.putNextEntry(new ZipEntry("book/index.rst"))
	zout.write(indexBuffer?.toString().bytes)
	
	zout.close()

	//response.sendRedirect 'aacsb.groovy'
	return
}

/*
  # with overline, for parts
  * with overline, for chapters
  =, for sections
  -, for subsections
  ^, for subsubsections
  ", for paragraphs
*/

html.doubleQuotes = true
html.html {
	head {
		meta ('http-equiv': "Content-Type", content: "text/html; charset=UTF-8")
		title ('AACSB匯出程式 - PLWeb')
		script (type: "text/javascript", '')
		link (rel: 'stylesheet', type: 'text/css', href: '../css/reset.css', media: 'all')
		link (rel: 'stylesheet', type: 'text/css', href: '../css/default.css', media: 'all')
		link (rel: 'stylesheet', type: 'text/css', href: 'aacsb.css', media: 'all')
	}
	body (class: 'page') {
		h1 ('系統管理區')
		div {
			a (href: 'javascript:location.reload()', '重新整理')
		}

		ul (class: 'menu-list') {
			sql.eachRow("select * from ST_CLASS where ALIVE='y' and SEMESTER in (1,3) order by CLASS_ID desc") {
				row->
				li {
					a (href: "aacsb.groovy?id=${row.CLASS_ID}", "${row.CLASS_NAME}")
					span ("${row.YEARS} / ${row.SEMESTER} / ${row.SCHOOL} / ${row.DEPARTMENT}")
				}
			}
		}
	}
}

/*
query1 = """
select *
from COURSE_FILE
where COURSE_ID=?
and VISIBLED='y'
order by SEQNUM
"""

row = sql.firstRow("select COURSE_NAME, COURSE_TITLE from COURSE where COURSE_ID=?", [course_id])
course_name  = row.course_name
course_title = row.course_title

response.setContentType("application/zip")
response.setHeader("Content-disposition", "attachment; filename=${course_name}.zip")

zout  = new ZipOutputStream(response.outputStream)

c = 0

sql.eachRow(query1, [course_id]) {
	row ->
	
	zout.putNextEntry(new ZipEntry("${course_id}/lesson${++c}.xml"))
	
	zout.write(row.text_xml.bytes)
}

zout.close()

sql.close()
*/