/*
 * Copyright 2013 Heatbud LLC. All Rights Reserved.
 * This software is the property of Heatbud LLC. No part of this source code may be
 * copied or distributed without the written permission from Heatbud LLC.
 */
package com.heatbud.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.heatbud.aws.HeatbudSESUtil;

/**
 * Utility class for handling files.
 */
public class HeatbudFileUtil {

	// SES client
    @Autowired
	private HeatbudSESUtil ses;

	/**
	 * This function scans catalina.out for lines containing the word SEVERE.
	 * Emails production@heatbud.com if found.
	 * @param Returns SUCCESS or FAILURE
	 */
	public String monitorTomcat () {

		String retMessage = "SUCCESS";
		boolean foundErrors = false;

		try {

			// Read offset
			int offset = 0;
			FileInputStream fo = null;
			BufferedReader bo = null;
			try {
				fo = new FileInputStream("/var/log/tomcat8/.offset.catalina.out");
				bo = new BufferedReader(new InputStreamReader(fo));
				offset = Integer.parseInt(bo.readLine());
			} catch (Exception e) {
			} finally {
				IOUtils.closeQuietly(bo);
				IOUtils.closeQuietly(fo);
			}

			// Read catalina.out
			FileInputStream fc = null;
			BufferedReader bc = null;
			try {
				fc = new FileInputStream("/var/log/tomcat8/catalina.out");
				bc = new BufferedReader(new InputStreamReader(fc));

				// Skip lines in catalina.out or reset offset
				PrintWriter po = null;
				boolean resetOffset = false;
				String nextLine = bc.readLine();
				try {
					int skippedLines = 1;
					while ( nextLine != null && skippedLines <= offset ) {
						skippedLines ++;
						nextLine = bc.readLine();
					}
					if ( skippedLines < offset ) {
						po = new PrintWriter("/var/log/tomcat8/.offset.catalina.out");
						po.write("0\n");
						resetOffset = true;
					}
				} catch (Exception e) {
					po = new PrintWriter("/var/log/tomcat8/.offset.catalina.out");
					po.write("0\n");
					resetOffset = true;
				} finally {
					IOUtils.closeQuietly(po);
				}

				// Reopen catalina.out in case we reset the offset above
				if ( resetOffset ) {
					offset = 0;
					IOUtils.closeQuietly(bc);
					IOUtils.closeQuietly(fc);
					fc = new FileInputStream("/var/log/tomcat8/catalina.out");
					bc = new BufferedReader(new InputStreamReader(fc));
				}

				// Read lines from catalina.out and process them
				String errorString = "";
				int printLineLocation = -1;
				while ( nextLine != null ) {
					offset++;
					if ( StringUtils.contains(nextLine, "SEVERE") ) {
						foundErrors = true;
						if ( printLineLocation == 1 ) {
							errorString = errorString + nextLine + "\n";
						} else {
							errorString = errorString + "\n\n" + nextLine + "\n";
						}
						printLineLocation = 0;
					}
					// print 50 lines starting from the error location
					if ( printLineLocation == 0 ) {
						printLineLocation++;
					} else if ( printLineLocation == 1 ) {
						errorString = errorString + nextLine + "\n";
						printLineLocation++;
					} else if ( printLineLocation > 1 && printLineLocation < 50 ) {
						errorString = errorString + nextLine + "\n";
						printLineLocation++;
					} else if ( printLineLocation == 50 ) {
						errorString = errorString + nextLine + "\n\n";
						printLineLocation = -1;
					}
					nextLine = bc.readLine();
				}

				// Write the final offset out
				try {
					po = new PrintWriter ("/var/log/tomcat8/.offset.catalina.out");
					po.write(offset+"\n");
				} catch (Exception e) {
				} finally {
					IOUtils.closeQuietly(po);
				}

				// Send email if errors found
				if ( foundErrors ) {
					ses.sendTomcatEmail(errorString, "catalina.out");
				}

			} catch (Exception e) {
			} finally {
				IOUtils.closeQuietly(bc);
				IOUtils.closeQuietly(fc);
			}

		} catch (Exception e) {
			retMessage = "FAILURE";
		}

		return retMessage;

	}

	/**
	 * This function scans localhost.yyyy-MM-dd.log for lines containing SEVERE.
	 * Emails production@heatbud.com if found.
	 * @param Returns SUCCESS or FAILURE
	 */
	public String monitorLocalhost () {

		String retMessage = "SUCCESS";
		boolean foundErrors = false;

		try {

			// Read offset
			int offset = 0;
			FileInputStream fo = null;
			BufferedReader bo = null;
			try {
				fo = new FileInputStream("/var/log/tomcat8/.offset.localhost.log");
				bo = new BufferedReader(new InputStreamReader(fo));
				offset = Integer.parseInt(bo.readLine());
			} catch (Exception e) {
			} finally {
				IOUtils.closeQuietly(bo);
				IOUtils.closeQuietly(fo);
			}

			// get current system date in the format yyyy-MM-dd
		    Date date = new Date();
		    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		    String formattedDate= dateFormat.format(date);

		    // Read localhost.log
			FileInputStream fc = null;
			BufferedReader bc = null;
			try {
				fc = new FileInputStream("/var/log/tomcat8/localhost."+formattedDate+".log");
				bc = new BufferedReader(new InputStreamReader(fc));

				// Skip lines in localhost.log or reset offset
				PrintWriter po = null;
				boolean resetOffset = false;
				String nextLine = bc.readLine();
				try {
					int skippedLines = 1;
					while ( nextLine != null && skippedLines <= offset ) {
						skippedLines ++;
						nextLine = bc.readLine();
					}
					if ( skippedLines < offset ) {
						po = new PrintWriter("/var/log/tomcat8/.offset.localhost.log");
						po.write("0\n");
						resetOffset = true;
					}
				} catch (Exception e) {
					po = new PrintWriter("/var/log/tomcat8/.offset.localhost.log");
					po.write("0\n");
					resetOffset = true;
				} finally {
					IOUtils.closeQuietly(po);
				}

				// Reopen localhost.log in case we reset the offset above
				if ( resetOffset ) {
					offset = 0;
					IOUtils.closeQuietly(bc);
					IOUtils.closeQuietly(fc);
					fc = new FileInputStream("/var/log/tomcat8/localhost."+formattedDate+".log");
					bc = new BufferedReader(new InputStreamReader(fc));
				}

				// Read lines from localhost.log and process them
				String errorString = "";
				int printLineLocation = -1;
				while ( nextLine != null ) {
					offset++;
					if ( StringUtils.contains(nextLine, "SEVERE") ) {
						foundErrors = true;
						if ( printLineLocation == 1 ) {
							errorString = errorString + nextLine + "\n";
						} else {
							errorString = errorString + "\n\n" + nextLine + "\n";
						}
						printLineLocation = 0;
					}
					// print 50 lines starting from the error location
					if ( printLineLocation == 0 ) {
						printLineLocation++;
					} else if ( printLineLocation == 1 ) {
						errorString = errorString + nextLine + "\n";
						printLineLocation++;
					} else if ( printLineLocation > 1 && printLineLocation < 50 ) {
						errorString = errorString + nextLine + "\n";
						printLineLocation++;
					} else if ( printLineLocation == 50 ) {
						errorString = errorString + nextLine + "\n\n";
						printLineLocation = -1;
					}
					nextLine = bc.readLine();
				}

				// Write the final offset out
				try {
					po = new PrintWriter ("/var/log/tomcat8/.offset.localhost.log");
					po.write(offset+"\n");
				} catch (Exception e) {
				} finally {
					IOUtils.closeQuietly(po);
				}

				// Send email if errors found
				if ( foundErrors ) {
					ses.sendTomcatEmail(errorString, "localhost.log");
				}

			} catch (Exception e) {
			} finally {
				IOUtils.closeQuietly(bc);
				IOUtils.closeQuietly(fc);
			}

		} catch (Exception e) {
			retMessage = "FAILURE";
		}

		return retMessage;

	}

}
