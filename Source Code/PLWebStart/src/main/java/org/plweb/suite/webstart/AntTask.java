package org.plweb.suite.webstart;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.taskdefs.Zip;

public class AntTask {
	static public void zipDir(File destFile, File zipFile) {
		final class AntZIP extends Zip {
			@SuppressWarnings("deprecation")
			public AntZIP() {
				project = new Project();
				project.init();
				taskType = "zip";
				taskName = "zip";
				target = new Target();
			}
		}

		AntZIP zip = new AntZIP();
		zip.setBasedir(zipFile);
		zip.setDestFile(destFile);
		zip.execute();
	}

	static public void unzipFile(File destFile, File zipFile) {
		final class Expander extends Expand {
			@SuppressWarnings("deprecation")
			public Expander() {
				project = new Project();
				project.init();
				taskType = "unzip";
				taskName = "unzip";
				target = new Target();
			}
		}

		Expander expander = new Expander();
		expander.setSrc(zipFile);
		expander.setDest(destFile);
		expander.execute();
	}

	static public void delDir(File dir) {
		final class AntDelete extends Delete {
			@SuppressWarnings("deprecation")
			public AntDelete() {
				project = new Project();
				project.init();
				taskType = "delete";
				taskName = "delete";
				target = new Target();
			}
		}

		AntDelete ant = new AntDelete();
		ant.setDir(dir);
		ant.execute();
	}

	static public void copyFile(File src, File obj) {
		final class AntCopy extends Copy {
			@SuppressWarnings("deprecation")
			public AntCopy() {
				project = new Project();
				project.init();
				taskType = "copy";
				taskName = "copy";
				target = new Target();
			}
		}

		AntCopy ant = new AntCopy();
		ant.setFile(src);
		ant.setTofile(obj);
		ant.execute();
	}

}
