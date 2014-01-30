/*
 * Copyright (C) 2014 Diganth Aswath <diganth2004@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package soapUIScripts

import com.eviware.soapui.impl.wsdl.panels.support.MockTestRunContext
import com.eviware.soapui.model.project.ProjectFactoryRegistry
import com.eviware.soapui.model.support.ModelSupport
import com.eviware.soapui.support.UISupport
import com.eviware.soapui.LogMonitor.*

/**
 *
 * @author Diganth Aswath <diganth2004@gmail.com>
 */
class captureURL {
	
	captureURL(){
		
	}
	def modURLString(url){
		url.replaceFirst(/^https/, "http")
	}
	
	//Function to create a Text File with TestStep name
	def file_create (testStepname, type, url, arg1){
		def fileName = dirName+testStepname+'_'+arg1+type
		// Write output to file created
		writetoFile (fileName, url)
	}
	
	// Function to write responseURL to the file.
	def writetoFile (fileName,  url){
		//def file = new File(fileName)
		//file.write(url, "UTF-8") //Writing response into the file created
		modded_url = modURLString(url)
		def URLimgfile = new FileOutputStream(fileName)
		def out = new BufferedOutputStream(URLimgfile)
		//log.info new URL(url).openStream()
		try{
			out << new URL(url).openStream()
		} catch (e){
			out << new URL(modded_url).openStream()
		}
		out.close()
	}
	
	// Function that controls the logic of iterating through the testSteps to obtain
	// URL from the response.
	def printURL (arg1) {
		logFile << "${today}:: ARGUMENT :: ${arg1}" <<"\r\n"
		def testSteps = myTestCase.getTestStepList()
		testSteps.each {
			// Checking if TestStep is of WSDLTestRequest type
			if (it instanceof com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep){
				// Reading Raw request to extract Namespace to use.
				def rawRequest = it.getProperty("Request").getValue()
				if (rawRequest.contains("CreateIndicium")){
					String[] nameSpaceURL= rawRequest.findAll('https?://[^\\s<>"]+|www\\.[^\\s<>"]+')
					// Reading response content into an object
					def url = context.expand( '${'+it.name+'#Response#declare namespace ns1=\''+nameSpaceURL[1]+'\';//ns1:CreateIndiciumResponse[1]/ns1:URL[1]}')
					// Checking if URL string is empty
					if(url?.trim()) {
						logFile << "${today}::INFO::Captured label for -> ${it.name} -> ${arg1}" <<"\r\n"
						if(url.contains(".pdf")) {
							file_create (it.name, ".pdf", url, arg1)
						}
						else if (url.contains(".png")){
							file_create (it.name, ".png", url, arg1)
						}
					}
					else {
						logFile << "${today}::ERROR::Unable to capture label for -> ${it.name} ->${arg1}" <<"\r\n"
					}
			 }
			}
		}
	}
	
	
}
