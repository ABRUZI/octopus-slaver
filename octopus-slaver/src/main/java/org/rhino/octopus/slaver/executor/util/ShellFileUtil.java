package org.rhino.octopus.slaver.executor.util;

import java.io.File;
import java.util.UUID;

import org.rhino.octopus.base.configuration.OctopusConfiguration;
import org.rhino.octopus.base.exception.OctopusException;
import org.rhino.octopus.slaver.context.SlaverContext;

public class ShellFileUtil {

	
	public static String createShellFile(String fileName, File file)throws OctopusException{
		String dir = SlaverContext.getInstance().getConfiguration().getProperty(OctopusConfiguration.ConfigurationItem.SLAVER_SHELL_FILE_DIR).getValue();
		String fullName = dir + File.separatorChar +  getRandomPrefix() + fileName;
		File shellFile = new File(fullName);
		file.renameTo(shellFile);
		shellFile.setExecutable(true, true);
		shellFile.setReadable(true, false);
		shellFile.setWritable(true, true);
		return fullName;
	} 
	
	public static final void deleteShellFile(String fullName){
		new File(fullName).delete();
	}
	
	
	private static String getRandomPrefix(){
		return UUID.randomUUID().toString();
	}
}
