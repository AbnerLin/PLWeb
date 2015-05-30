import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response)

class_id = helper.fetch('class_id')

helper.attr('htmlhead', helper.htmlhead('../'))
helper.attr('preview_url', "../schedule.groovy?id=${class_id}");
helper.attr('category_url', "schedule_detail.groovy?class_id=${class_id}");

helper.forward('schedule.gsp')