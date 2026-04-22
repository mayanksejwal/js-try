package com.newgen.PC;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Properties;

import com.itextpdf.awt.geom.Rectangle;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.newgen.common.CommonConnection;
import com.newgen.common.CommonMethods;

public class GeneratePDF {
	
	public static String PDFTemplate(String WINAME, String ActivityName, String IntegrationCall, String DocName, HashMap<String, String> GridDataMap)
	{
		PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", inside PDFTemplate function for IntegrationCall : "+IntegrationCall);
		 
		String RetValue = "";
		if(IntegrationCall.equals("DEDUP_SUMMARY"))
		{
			 PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", inside onclick function for attach Dedupe click");
			 											 			
				//PDF Generation****************************************
				try{
			        String Xmlout="";

					String Inputcustfirstname = "";
					String Inputcustlastname = "";
					String Inputcustdob = "";
					String Inputcustnationality = "";
					String InputcustTLNumber = "";
					String CompFlag = GridDataMap.get("COMPANYFLAG");
			      
		        	
		        	if("R".equalsIgnoreCase(CompFlag))
					{
		        		Inputcustfirstname = GridDataMap.get("FIRSTNAME")+" "+GridDataMap.get("MIDDLENAME");
						Inputcustlastname = GridDataMap.get("LASTNAME");
						Inputcustdob = GridDataMap.get("DATEOFBIRTH");
						Inputcustnationality = GridDataMap.get("NATIONALITY");
					}
		        	else if("C".equalsIgnoreCase(CompFlag))
		        	{
		        		Inputcustfirstname = GridDataMap.get("COMPANY_NAME");
						Inputcustlastname = "";
						Inputcustdob = GridDataMap.get("DATEOFINCORPORATION");
						Inputcustnationality = GridDataMap.get("COUNTRY");
						InputcustTLNumber= GridDataMap.get("TL_NUMBER");
		        	}	
		        	
					String path = System.getProperty("user.dir");
					String pdfTemplatePath = "";
					String generatedPdfPath = "";
												
					String imgPath = "";
					String generatedimgPath = "";					

					String dynamicPdfName =  WINAME+ DocName + ".pdf";
					
					//Reading path from property file
					Properties properties = new Properties();
					properties.load(new FileInputStream(System.getProperty("user.dir")+ System.getProperty("file.separator")+ "ConfigFiles" + System.getProperty("file.separator")+ "PC_Config.properties"));
										
					pdfTemplatePath = path + pdfTemplatePath;//Getting complete path of the pdf template
					generatedPdfPath = properties.getProperty("PC_GENERTATED_PDF_PATH");//Get the location of the path where generated template will be saved
					generatedPdfPath += dynamicPdfName;
					generatedPdfPath = path + generatedPdfPath;//Complete path of generated PDF
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \nDedup GeneratedPdfPath :" + generatedPdfPath);
											
					FileOutputStream fileOutputStream = new FileOutputStream(generatedPdfPath);
					com.itextpdf.text.Document doc = new com.itextpdf.text.Document(PageSize.A4.rotate());
		            PdfWriter writer = PdfWriter.getInstance(doc, fileOutputStream);
		            writer.open();
		            doc.open();
		            Font bold = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
					
		            Paragraph preface = new Paragraph();
		            preface = new Paragraph("Omniflow Reference No     :   "+WINAME,bold);
					preface.setAlignment(Element.ALIGN_JUSTIFIED);
		            doc.add(preface);							
		           
		            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 1:");
					preface = new Paragraph("Input Parameters",bold);
					preface.setAlignment(Element.ALIGN_JUSTIFIED);
		            doc.add(preface);
		            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 2:");
					/*preface = new Paragraph("CIF ID     :   "+CIF_ID);
					preface.setAlignment(Element.ALIGN_JUSTIFIED);
		            doc.add(preface);*/
		        	if("C".equalsIgnoreCase(CompFlag)) 
		        	{
		        		preface = new Paragraph("Company Name     :   "+Inputcustfirstname);
						preface.setAlignment(Element.ALIGN_JUSTIFIED);
						doc.add(preface);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 3:");
						preface = new Paragraph("Date of Incorporation     :   "+Inputcustdob);
						preface.setAlignment(Element.ALIGN_JUSTIFIED);
						doc.add(preface);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 4:");
						preface = new Paragraph("Country of Incorporation     :   "+Inputcustnationality);
						preface.setAlignment(Element.ALIGN_JUSTIFIED);
						doc.add(preface);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 5:");
						preface = new Paragraph("TL Number     :   "+InputcustTLNumber);
						preface.setAlignment(Element.ALIGN_JUSTIFIED);
						doc.add(preface);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 6:");
		        	}
		        	else
		        	{
		        	
					    preface = new Paragraph("First Name     :   "+Inputcustfirstname);
						preface.setAlignment(Element.ALIGN_JUSTIFIED);
						doc.add(preface);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 3:");
						preface = new Paragraph("Last Name     :   "+Inputcustlastname);
						preface.setAlignment(Element.ALIGN_JUSTIFIED);
						doc.add(preface);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 4:");
						preface = new Paragraph("DOB     :   "+Inputcustdob);
						preface.setAlignment(Element.ALIGN_JUSTIFIED);
						doc.add(preface);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 5:");
						preface = new Paragraph("Nationality     :   "+Inputcustnationality);
						preface.setAlignment(Element.ALIGN_JUSTIFIED);
						doc.add(preface);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 6:");
		        	}
					
					preface=new Paragraph("   ",bold);
		            preface.setAlignment(Element.ALIGN_CENTER);
		            doc.add(preface);
		            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 7:");
		            preface=new Paragraph("DEDUPE CHECK",bold);
		            preface.setAlignment(Element.ALIGN_CENTER);
		            doc.add(preface);
		            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 8:");
					preface=new Paragraph("   ",bold);
		            preface.setAlignment(Element.ALIGN_CENTER);
		            doc.add(preface);
		            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 9:");
		            PdfPTable pdf = new PdfPTable(7);
					if("C".equalsIgnoreCase(CompFlag)) 
		        	{
						pdf = new PdfPTable(7);
				        PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable :");
						pdf.setHorizontalAlignment(Element.ALIGN_CENTER);
						int[] columnWidths = {8,7,20,15,15,15,20};
						pdf.setWidths(columnWidths);
						pdf.setWidthPercentage(100);
						
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 1:");
			            //PdfPCell c1 = new PdfPCell(new Phrase("CIFID"));
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 2:");
						Font fbold1 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
			            PdfPCell c1 = new PdfPCell(new Phrase("CIFID",fbold1));
			            //System.out.println("Prepared");
			            c1.setBackgroundColor(new BaseColor(153, 0, 51));
						c1.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 8:");
						Font fbold8 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
			            PdfPCell c8 = new PdfPCell(new Phrase("CIFStatus",fbold8));
			            //System.out.println("Prepared");
			            c8.setBackgroundColor(new BaseColor(153, 0, 51));
						c8.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 3:");
			            //PdfPCell c1 = new PdfPCell(new Phrase("CIFID"));
						Font fbold2 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
			            PdfPCell c2 = new PdfPCell(new Phrase("CompanyName",fbold2));
			            //System.out.println("Prepared");
			            c2.setBackgroundColor(new BaseColor(153, 0, 51));
						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						
						//PdfPCell c2 = new PdfPCell(new Phrase("CIFStatus"));
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 4:");
						Font fbold3 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
			            PdfPCell c3 = new PdfPCell(new Phrase("DateOfIncorporation",fbold3));
			           //System.out.println("Prepared");
			            c3.setBackgroundColor(new BaseColor(153, 0, 51));
						c3.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						
			            //PdfPCell c3 = new PdfPCell(new Phrase("FullName"));
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 5:");
						Font fbold4 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
			            PdfPCell c4 = new PdfPCell(new Phrase("CountryOfIncorporation",fbold4));
			            //System.out.println("Prepared");
			            c4.setBackgroundColor(new BaseColor(153, 0, 51));
						c4.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			            //PdfPCell c4 = new PdfPCell(new Phrase("EmiratesID"));
						
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 6:");
						Font fbold5 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
			            PdfPCell c5 = new PdfPCell(new Phrase("TL Number",fbold5));
			            //System.out.println("Prepared");
			            c5.setBackgroundColor(new BaseColor(153, 0, 51));
						c5.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 8:");
						Font fbold7 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
			            PdfPCell c7 = new PdfPCell(new Phrase("Phone",fbold7));
			            //System.out.println("Prepared");
			            c7.setBackgroundColor(new BaseColor(153, 0, 51));
						c7.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						//PdfPCell c6 = new PdfPCell(new Phrase("BFlag"));
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 9:");  
						
						try
						{
							PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append:");  
							//pdf.addCell(h1);
							PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 1:");  
				            pdf.addCell(c1);
				            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 2:");  
				            pdf.addCell(c8);
				            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 3:");  
				            pdf.addCell(c2);
				            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 4 :");  
				            pdf.addCell(c3);
				            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 5:");  
				            pdf.addCell(c4);
				            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 6:");  
							pdf.addCell(c5);
							PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 7:");  
				           // pdf.addCell(c6);
				           // PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 8:");  
							pdf.addCell(c7);
							PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 9:");  
						}
						catch(Exception e)
						{
							PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", In catch After image : "+e.getStackTrace());
						}
						
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 10:");
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", CIF_ID.size() "+PCIntegration.DedupeGridCIFID.size());
						for (int j = 0; j < PCIntegration.DedupeGridCIFID.size(); j++) {
									
			                c1 = new PdfPCell(new Phrase(PCIntegration.DedupeGridCIFID.get(j)));
							PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", Aftr CIFIDarray for WINAME "+WINAME+" : "+PCIntegration.DedupeGridCIFID.get(j));
							c1.setBackgroundColor(new BaseColor(255,251,240));
							c1.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			                pdf.addCell(c1);
			                
			                c8 = new PdfPCell(new Phrase(PCIntegration.DedupeGridCIFStatus.get(j)));
							PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", Aftr CIFIDarray for WINAME "+WINAME+" : "+PCIntegration.DedupeGridCIFStatus.get(j));
							c8.setBackgroundColor(new BaseColor(255,251,240));
							c8.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			                pdf.addCell(c8);
			                
			                c2 = new PdfPCell(new Phrase(PCIntegration.DedupeGridFullName.get(j)));
			                PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", Aftr fullNamearray for WINAME "+WINAME+" : "+PCIntegration.DedupeGridFullName.get(j));
							c2.setBackgroundColor(new BaseColor(255,251,240));
							c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			                pdf.addCell(c2);
							
							c3 = new PdfPCell(new Phrase(PCIntegration.DedupeGridIncorporationDate.get(j)));
			                PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", dateOfIncorporation for WINAME "+WINAME+" : "+PCIntegration.DedupeGridIncorporationDate.get(j));
							c3.setBackgroundColor(new BaseColor(255,251,240));
							c3.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			                pdf.addCell(c3);
							
							
							c4 = new PdfPCell(new Phrase(PCIntegration.DedupeGridIncorporationCountry.get(j)));
							//System.out.println("Aftr EmiratesIDarray");
							PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n CountryOfIncorporation value for WINAME "+WINAME+" : "+PCIntegration.DedupeGridIncorporationCountry.get(j));
							c4.setBackgroundColor(new BaseColor(255,251,240));
							c4.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
							pdf.addCell(c4);
							
							c5 = new PdfPCell(new Phrase(PCIntegration.DedupeGridIncorporationTLNo.get(j)));
			                //System.out.println("Aftr PassportNumberarray");
							PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n TL No value for WINAME "+WINAME+" : "+PCIntegration.DedupeGridIncorporationTLNo.get(j));
							c5.setBackgroundColor(new BaseColor(255,251,240));
							c5.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			                pdf.addCell(c5);
								
			                c7 = new PdfPCell(new Phrase(PCIntegration.DedupeGridMobNo.get(j)));
							PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n Phonearray value for WINAME "+WINAME+" : "+PCIntegration.DedupeGridMobNo.get(j));
							c7.setBackgroundColor(new BaseColor(255,251,240));
							 PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n PDF Generated check for WINAME "+WINAME);
							c7.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			                pdf.addCell(c7);				
						     PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n PDF Generated check 1 for WINAME "+WINAME);           
			            }
		        	}
					else
					{
						pdf = new PdfPTable(8);
				        PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable :");
				        pdf.setHorizontalAlignment(Element.ALIGN_CENTER);
						int[] columnWidths = {9,9,20,12,12,10,12,16};
						pdf.setWidths(columnWidths);
						pdf.setWidthPercentage(100);
						
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 1:");
			            //PdfPCell c1 = new PdfPCell(new Phrase("CIFID"));
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 2:");
						Font fbold1 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
			            PdfPCell c1 = new PdfPCell(new Phrase("CIFID",fbold1));
			            //System.out.println("Prepared");
			            c1.setBackgroundColor(new BaseColor(153, 0, 51));
						c1.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 8:");
						Font fbold8 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
			            PdfPCell c8 = new PdfPCell(new Phrase("CIFStatus",fbold8));
			            //System.out.println("Prepared");
			            c8.setBackgroundColor(new BaseColor(153, 0, 51));
						c8.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						
						
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 3:");
			            //PdfPCell c1 = new PdfPCell(new Phrase("CIFID"));
						Font fbold2 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
			            PdfPCell c2 = new PdfPCell(new Phrase("FullName",fbold2));
			            c2.setBackgroundColor(new BaseColor(153, 0, 51));
						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 5:");
						
						Font fbold4 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
			            PdfPCell c4 = new PdfPCell(new Phrase("EmiratesID",fbold4));
			            c4.setBackgroundColor(new BaseColor(153, 0, 51));
						c4.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 6:");
						
						Font fbold5 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
			            PdfPCell c5 = new PdfPCell(new Phrase("PassportNumber",fbold5));
			            c5.setBackgroundColor(new BaseColor(153, 0, 51));
						c5.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 4:");
						
						Font fbold3 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
			            PdfPCell c3 = new PdfPCell(new Phrase("DOB",fbold3));
			            c3.setBackgroundColor(new BaseColor(153, 0, 51));
						c3.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 7:");
						
						Font fbold6 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
			            PdfPCell c6 = new PdfPCell(new Phrase("Nationality",fbold6));
			            c6.setBackgroundColor(new BaseColor(153, 0, 51));
						c6.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 8:");
						Font fbold7 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
			            PdfPCell c7 = new PdfPCell(new Phrase("Phone",fbold7));
			            c7.setBackgroundColor(new BaseColor(153, 0, 51));
						c7.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 9:");  
					
						try
						{
							PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append:");  
							//pdf.addCell(h1);
							PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 1:");  
				            pdf.addCell(c1);
				            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 2:");  
				            pdf.addCell(c8);
				            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 3:");  
				            pdf.addCell(c2);
				            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 4 :");  
				            pdf.addCell(c4);
				            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 5:");  
				            pdf.addCell(c5);
				            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 6:");  
							pdf.addCell(c3);
							PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 7:");  
				            pdf.addCell(c6);
				            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 8:");  
							pdf.addCell(c7);
							PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 9:");  
						}
						catch(Exception e)
						{
							PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", In catch After image : "+e.getStackTrace());
						}
						
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 10:");
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", CIF_ID.size() "+PCIntegration.DedupeGridCIFID.size());
						for (int j = 0; j < PCIntegration.DedupeGridCIFID.size(); j++) {
							
			                c1 = new PdfPCell(new Phrase(PCIntegration.DedupeGridCIFID.get(j)));
							PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", Aftr CIFIDarray for WINAME "+WINAME+" : "+PCIntegration.DedupeGridCIFID.get(j));
							c1.setBackgroundColor(new BaseColor(255,251,240));
							c1.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			                pdf.addCell(c1);
			                
			                c8 = new PdfPCell(new Phrase(PCIntegration.DedupeGridCIFStatus.get(j)));
							PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", Aftr CIFIDarray for WINAME "+WINAME+" : "+PCIntegration.DedupeGridCIFStatus.get(j));
							c8.setBackgroundColor(new BaseColor(255,251,240));
							c8.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			                pdf.addCell(c8);
			                
			                c2 = new PdfPCell(new Phrase(PCIntegration.DedupeGridFullName.get(j)));
			                PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", Aftr fullNamearray for WINAME "+WINAME+" : "+PCIntegration.DedupeGridFullName.get(j));
							c2.setBackgroundColor(new BaseColor(255,251,240));
							c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			                pdf.addCell(c2);
			                

							try {
								c4 = new PdfPCell(new Phrase(PCIntegration.DedupeGridEmiratesID.get(j)));
								//System.out.println("Aftr EmiratesIDarray");
								PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n EmiratesIDarray value for WINAME "+WINAME+" : "+PCIntegration.DedupeGridEmiratesID.get(j));
								c4.setBackgroundColor(new BaseColor(255,251,240));
								c4.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
								pdf.addCell(c4);
							} catch (Exception e){
								
							}
							
							c5 = new PdfPCell(new Phrase(PCIntegration.DedupeGridPassportNo.get(j)));
			                //System.out.println("Aftr PassportNumberarray");
							PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n PassportNumberarray value for WINAME "+WINAME+" : "+PCIntegration.DedupeGridPassportNo.get(j));
							c5.setBackgroundColor(new BaseColor(255,251,240));
							c5.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			                pdf.addCell(c5);
							
							c3 = new PdfPCell(new Phrase(PCIntegration.DedupeGridDOB.get(j)));
			                PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", Aftr DOBarray for WINAME "+WINAME+" : "+PCIntegration.DedupeGridDOB.get(j));
							c3.setBackgroundColor(new BaseColor(255,251,240));
							c3.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			                pdf.addCell(c3);
							
							c6 = new PdfPCell(new Phrase(PCIntegration.DedupeGridNationality.get(j)));
			                //System.out.println("aftr Nationalityarray");
							PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n Nationalityarray value for WINAME "+WINAME+" : "+PCIntegration.DedupeGridNationality.get(j));
							c6.setBackgroundColor(new BaseColor(255,251,240));
							c6.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			                pdf.addCell(c6);
			                							
			                c7 = new PdfPCell(new Phrase(PCIntegration.DedupeGridMobNo.get(j)));
			                //System.out.println("aftr Phonearray");
							PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n Phonearray value for WINAME "+WINAME+" : "+PCIntegration.DedupeGridMobNo.get(j));
							c7.setBackgroundColor(new BaseColor(255,251,240));
							 PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n PDF Generated check for WINAME "+WINAME);
							c7.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			                pdf.addCell(c7);				
						     PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n PDF Generated check 1 for WINAME "+WINAME);           
							// doc.add(pdf);
			            }
					}
		            doc.add(pdf);								
		            doc.close();
		            
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n PDF Generated Successfully in target location for WINAME "+WINAME);
					String Response=CommonMethods.AttachDocumentWithWI(CommonConnection.getCabinetName(),WINAME, CommonConnection.getSessionID(PCLog.PCLogger, false),CommonConnection.getJTSIP(),Integer.parseInt(CommonConnection.getsSMSPort()),generatedPdfPath,DocName);
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n Response : "+Response);
					//***********DeleteFile*************
					String strStatus = CommonMethods.DeleteFile(generatedPdfPath);
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n strStatus of deleting the file in dedupe : "+strStatus);
					//**************************************						
					RetValue = Response;
				}
				catch(Exception e)
				{
					e.printStackTrace();
					//sMappOutPutXML="Exception"+e;
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", in catch of DedupeGeneratePDF Exception is: "+e);
					
					RetValue = "in catch of DedupeGeneratePDF Exception is: "+e;
				}
		
		}
		else if(IntegrationCall.equals("BLACKLIST_DETAILS"))
		{
			PCLog.PCLogger.debug("inside Blacklist PDF");
			//PDF Generation****************************************
			try
			{
		        String Xmlout="";				
		        String Inputcustfirstname = "";
				String Inputcustlastname = "";
				String Inputcustdob = "";
				String Inputcustnationality = "";
				String InputcustTLNo="";
				String CompFlag = GridDataMap.get("COMPANYFLAG");
	        	
	        	if("R".equalsIgnoreCase(CompFlag))
				{
	        		Inputcustfirstname = GridDataMap.get("FIRSTNAME")+" "+GridDataMap.get("MIDDLENAME");
					Inputcustlastname = GridDataMap.get("LASTNAME");
					Inputcustdob = GridDataMap.get("DATEOFBIRTH");
					Inputcustnationality = GridDataMap.get("NATIONALITY");
				}
	        	else if("C".equalsIgnoreCase(CompFlag))
	        	{
	        		Inputcustfirstname = GridDataMap.get("COMPANY_NAME");
					Inputcustlastname = "";
					Inputcustdob = GridDataMap.get("DATEOFINCORPORATION");
					Inputcustnationality = GridDataMap.get("COUNTRY");
					InputcustTLNo=GridDataMap.get("TL_NUMBER");	
	        	}			        	
		        													
				String path = System.getProperty("user.dir");	
				String pdfTemplatePath = "";
				String generatedPdfPath = "";
				
				String imgPath = "";
				String generatedimgPath = "";
				String dynamicPdfName =  WINAME+ DocName + ".pdf";

				//Reading path from property file
				Properties properties = new Properties();
				properties.load(new FileInputStream(System.getProperty("user.dir")+ System.getProperty("file.separator")+ "ConfigFiles" + System.getProperty("file.separator")+ "PC_Config.properties"));
					
				pdfTemplatePath = path + pdfTemplatePath;//Getting complete path of the pdf template
				generatedPdfPath = properties.getProperty("PC_GENERTATED_PDF_PATH");//Get the loaction of the path where generated template will be saved
				generatedPdfPath += dynamicPdfName;
				generatedPdfPath = path + generatedPdfPath;//Complete path of generated PDF
				
				PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \nBlack List GeneratedPdfPath :" + generatedPdfPath);
								
				FileOutputStream fileOutputStream = new FileOutputStream(generatedPdfPath);
				com.itextpdf.text.Document doc = new com.itextpdf.text.Document(PageSize.A4.rotate());
	            PdfWriter writer = PdfWriter.getInstance(doc, fileOutputStream);
	            writer.open();
	            doc.open();
	            Font bold = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
				
				String dynamicimgName = "bank-logo.gif";
				generatedimgPath = properties.getProperty("PC_LoadLogo");
				generatedimgPath += dynamicimgName;
				generatedimgPath = path + generatedimgPath;
				PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \nBlack List generatedimgPath :" + generatedimgPath);								
				
	            Paragraph preface = new Paragraph();
				//generatedimgPath=generatedimgPath.replace("/","//");
				PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \nBlack List generatedimgPath after replace:" + generatedimgPath);
				Image img = Image.getInstance(generatedimgPath);
				
	            img.setAlignment(Image.ALIGN_RIGHT);  
	            img.scaleAbsolute(60f, 40f);
				
	            preface.add(img);
	            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After image");
	            
	            doc.add(preface);
				preface = new Paragraph("Omniflow Reference No     :   "+WINAME,bold);
				preface.setAlignment(Element.ALIGN_JUSTIFIED);
	            doc.add(preface);							
	            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After image 1:");
	            
				preface = new Paragraph("Input Parameters",bold);
				preface.setAlignment(Element.ALIGN_JUSTIFIED);
	            doc.add(preface);
	            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 1:");
	            
	            if("C".equalsIgnoreCase(CompFlag) ) 
	            {
	            	preface = new Paragraph("Company Name :   "+Inputcustfirstname);
					preface.setAlignment(Element.ALIGN_JUSTIFIED);
					doc.add(preface);
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 2:");
					
					preface= new Paragraph("Date of Incorporation: "+Inputcustdob);
		            preface.setAlignment(Element.ALIGN_JUSTIFIED);
		            doc.add(preface);
		            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 5:");
		            
	             	preface= new Paragraph("Country of Incorporation:  "+Inputcustnationality);
		            preface.setAlignment(Element.ALIGN_JUSTIFIED);
		            doc.add(preface);
		            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 6:");
		            
		            preface= new Paragraph("TL Number:  "+InputcustTLNo);
		            preface.setAlignment(Element.ALIGN_JUSTIFIED);
		            doc.add(preface);
		            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 6:");
	            	
	            }
	            else
				{
	             	preface = new Paragraph("First Name :   "+Inputcustfirstname);
					preface.setAlignment(Element.ALIGN_JUSTIFIED);
					doc.add(preface);
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 2:");
					
					preface = new Paragraph("Last Name :   "+Inputcustlastname);
					preface.setAlignment(Element.ALIGN_JUSTIFIED);
					doc.add(preface);
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 4:");
					preface= new Paragraph("DOB: "+Inputcustdob);
		            preface.setAlignment(Element.ALIGN_JUSTIFIED);
		            doc.add(preface);
		            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 5:");
		            
	             	preface= new Paragraph("Nationality:  "+Inputcustnationality);
		            preface.setAlignment(Element.ALIGN_JUSTIFIED);
		            doc.add(preface);
		            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 6:");
				}
				
	            preface= new Paragraph("   ",bold);
	            preface.setAlignment(Element.ALIGN_CENTER);
	            doc.add(preface);
	            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 7:");
	            
	            preface=new Paragraph("Black List CHECK",bold);
	            preface.setAlignment(Element.ALIGN_CENTER);
	            doc.add(preface);
	            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 8:");
	            
	            preface=new Paragraph("   ",bold);
	            preface.setAlignment(Element.ALIGN_CENTER);
	            doc.add(preface);
	            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 9:");
	            
	            
	            PdfPTable pdf = new PdfPTable(11);
	            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable :");
				pdf.setHorizontalAlignment(Element.ALIGN_CENTER);
				int[] columnWidths = {7,7,14,10,9,8,11,5,12,5,12};
				//int[] columnWidths = {50,150,250,250,200,200,200,120,200,120,120,120};
				pdf.setWidths(columnWidths);
	         	pdf.setWidthPercentage(100);
	         	
				PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 1:");
				
	            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 2:");
	            
				Font fbold1 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
	            PdfPCell c1 = new PdfPCell(new Phrase("CIFID",fbold1));
	            c1.setBackgroundColor(new BaseColor(153, 0, 51));
				c1.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				
				PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 3:");
				
	        
				Font fbold2 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
	            PdfPCell c2 = new PdfPCell(new Phrase("CIFStatus",fbold2));
	            c2.setBackgroundColor(new BaseColor(153, 0, 51));
				c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				
				PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 4:");
				
				  
	            if("C".equalsIgnoreCase(CompFlag)) 
	            {
	        		Font fbold3 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
		            PdfPCell c3 = new PdfPCell(new Phrase("Company Name",fbold3));
		            c3.setBackgroundColor(new BaseColor(153, 0, 51));
					c3.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		        
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 5:");
					
					
					Font fbold4 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
		            PdfPCell c4 = new PdfPCell(new Phrase("Date of Incorporation",fbold4));
		            c4.setBackgroundColor(new BaseColor(153, 0, 51));
					c4.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		        
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 6:");
					
					Font fbold5 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
		            PdfPCell c5 = new PdfPCell(new Phrase("County of Incorporation",fbold5));
		            c5.setBackgroundColor(new BaseColor(153, 0, 51));
					c5.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 7:");
					
					Font fbold13 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
		            PdfPCell c13 = new PdfPCell(new Phrase("TL Number",fbold13));
		            c13.setBackgroundColor(new BaseColor(153, 0, 51));
		            c13.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 8:");
					
					Font fbold6 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
		            PdfPCell c6 = new PdfPCell(new Phrase("Phone",fbold6));
		            c6.setBackgroundColor(new BaseColor(153, 0, 51));
					c6.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 8:");
					
					Font fbold8 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
		            PdfPCell c8 = new PdfPCell(new Phrase("Blacklist Flag",fbold8));
		            c8.setBackgroundColor(new BaseColor(153, 0, 51));
					c8.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 10:");  
					
					Font fbold14 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
		            PdfPCell c14 = new PdfPCell(new Phrase("Blacklist Flag Date",fbold14));
		            c14.setBackgroundColor(new BaseColor(153, 0, 51));
		            c14.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 10:");  
					
					Font fbold12 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
		            PdfPCell c12 = new PdfPCell(new Phrase("Negated Flag",fbold12));
		            c12.setBackgroundColor(new BaseColor(153, 0, 51));
					c12.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					
					Font fbold15 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
		            PdfPCell c15 = new PdfPCell(new Phrase("Negation Flag Date",fbold15));
		            c15.setBackgroundColor(new BaseColor(153, 0, 51));
					c15.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 10:");  
					
					
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 14:");  
					try
					{
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append:");  
						//pdf.addCell(h1);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 1:");  
			            pdf.addCell(c1);
			            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 2:");  
			            pdf.addCell(c2);
			            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 3 :");  
			            pdf.addCell(c3);
			            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 4:");  
			            pdf.addCell(c4);
			            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 5:");  
						pdf.addCell(c5);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 13:");  
			            pdf.addCell(c13);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 6:");  
			            pdf.addCell(c6);
			            /*PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 7:");  
						pdf.addCell(c7);
						*/
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 8:");  
						pdf.addCell(c8);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 10:");  
						pdf.addCell(c14);
						//PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 9:"); 
						//pdf.addCell(c9);	
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 9:");  
						pdf.addCell(c12);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 12"
								+ ":");  
						pdf.addCell(c15);
						
					}
					catch(Exception e)
					{
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", In catch After image : "+e.getStackTrace());
					}
					
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 10:");
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", CIF_ID.size() "+PCIntegration.BlacklistGridCIFID.size());
					for (int j = 0; j < PCIntegration.BlacklistGridCIFID.size(); j++) {
						
		                c1 = new PdfPCell(new Phrase(PCIntegration.BlacklistGridCIFID.get(j)));
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", Aftr CIFIDarray for WINAME "+WINAME+" : "+PCIntegration.BlacklistGridCIFID.get(j));
						c1.setBackgroundColor(new BaseColor(255,251,240));
						c1.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		                pdf.addCell(c1);
		                
		                c2 = new PdfPCell(new Phrase(PCIntegration.BlacklistGridCifStatus.get(j)));
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", Aftr CIF Statusarray for WINAME "+WINAME+" : "+PCIntegration.BlacklistGridCifStatus.get(j));
						c2.setBackgroundColor(new BaseColor(255,251,240));
						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		                pdf.addCell(c2);
		                
		                c3 = new PdfPCell(new Phrase(PCIntegration.BlacklistGridFullName.get(j)));
		                PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", Aftr fullNamearray for WINAME "+WINAME+" : "+PCIntegration.BlacklistGridFullName.get(j));
						c3.setBackgroundColor(new BaseColor(255,251,240));
						c3.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		                pdf.addCell(c3);
						
						c4 = new PdfPCell(new Phrase(PCIntegration.BlacklistGridDateOfIncorp.get(j)));
						//System.out.println("Aftr EmiratesIDarray");
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n EmiratesIDarray value for WINAME "+WINAME+" : "+PCIntegration.BlacklistGridEmiratesID.get(j));
						c4.setBackgroundColor(new BaseColor(255,251,240));
						c4.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						pdf.addCell(c4); 
						
						c5 = new PdfPCell(new Phrase(PCIntegration.BlacklistGridCountryOfIncorp.get(j)));
		                //System.out.println("Aftr PassportNumberarray");
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n PassportNumberarray value for WINAME "+WINAME+" : "+PCIntegration.BlacklistGridPassportNo.get(j));
						c5.setBackgroundColor(new BaseColor(255,251,240));
						c5.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		                pdf.addCell(c5);
		                

		                c13 = new PdfPCell(new Phrase(PCIntegration.BlacklistGridTLNo.get(j)));
		                //System.out.println("aftr Phonearray");
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n Phonearray value for WINAME "+WINAME+" : "+PCIntegration.BlacklistGridMobNo.get(j));
						c13.setBackgroundColor(new BaseColor(255,251,240));
						c13.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		                pdf.addCell(c13);
						
		                c6 = new PdfPCell(new Phrase(PCIntegration.BlacklistGridMobNo.get(j)));
		                //System.out.println("aftr Phonearray");
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n Phonearray value for WINAME "+WINAME+" : "+PCIntegration.BlacklistGridMobNo.get(j));
						c6.setBackgroundColor(new BaseColor(255,251,240));
						c6.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		                pdf.addCell(c6);
		                
		           
		                c8 = new PdfPCell(new Phrase(PCIntegration.BlacklistGridBlacklistedFlag.get(j)));
		                PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n BlackListFlagarray value for WINAME "+WINAME+" : "+PCIntegration.BlacklistGridBlacklistedFlag.get(j));
						c8.setBackgroundColor(new BaseColor(255,251,240));
						c8.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		                pdf.addCell(c8);
		                
		                c14 = new PdfPCell(new Phrase(PCIntegration.BlacklistGridBlacklistedDate.get(j)));
		                PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n BlackListFlagarray value for WINAME "+WINAME+" : "+PCIntegration.BlacklistGridBlacklistedFlag.get(j));
		                c14.setBackgroundColor(new BaseColor(255,251,240));
		                c14.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		                pdf.addCell(c14);
		                
		            	
		                c12 = new PdfPCell(new Phrase(PCIntegration.BlacklistGridNegatedFlag.get(j)));
		                PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n NEGATED_FLAGarray value for WINAME "+WINAME+" : "+PCIntegration.BlacklistGridNegatedFlag.get(j));
						c12.setBackgroundColor(new BaseColor(255,251,240));
						c12.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		                pdf.addCell(c12);
		                
		                c15 = new PdfPCell(new Phrase(PCIntegration.BlacklistGridNegatedDate.get(j)));
		                PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n NEGATED_FLAGarray value for WINAME "+WINAME+" : "+PCIntegration.BlacklistGridNegatedFlag.get(j));
		                c15.setBackgroundColor(new BaseColor(255,251,240));
		                c15.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		                pdf.addCell(c15);
		                
		            	 PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n PDF Generated check 1 for WINAME "+WINAME);           
						 //doc.add(pdf);
		            }
	            	
	            }
	            else
	            {
					Font fbold3 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
		            PdfPCell c3 = new PdfPCell(new Phrase("FullName",fbold3));
		            c3.setBackgroundColor(new BaseColor(153, 0, 51));
					c3.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		        
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 5:");
					
					
					Font fbold4 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
		            PdfPCell c4 = new PdfPCell(new Phrase("EmiratesID",fbold4));
		            c4.setBackgroundColor(new BaseColor(153, 0, 51));
					c4.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		        
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 6:");
					
					Font fbold5 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
		            PdfPCell c5 = new PdfPCell(new Phrase("Passport Number",fbold5));
		            c5.setBackgroundColor(new BaseColor(153, 0, 51));
					c5.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 7:");
					
					Font fbold13 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
		            PdfPCell c13 = new PdfPCell(new Phrase("Date of Birth",fbold13));
		            c13.setBackgroundColor(new BaseColor(153, 0, 51));
		            c13.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 7:");
					
					Font fbold6 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
		            PdfPCell c6 = new PdfPCell(new Phrase("Phone",fbold6));
		            c6.setBackgroundColor(new BaseColor(153, 0, 51));
					c6.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 8:");
					
	
					Font fbold8 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
		            PdfPCell c8 = new PdfPCell(new Phrase("Blacklist Flag",fbold8));
		            c8.setBackgroundColor(new BaseColor(153, 0, 51));
					c8.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 10:");  
					
					Font fbold14 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
		            PdfPCell c14 = new PdfPCell(new Phrase("Blacklist Flag Date",fbold14));
		            c14.setBackgroundColor(new BaseColor(153, 0, 51));
		            c14.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 10:");  
					
					Font fbold12 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
		            PdfPCell c12 = new PdfPCell(new Phrase("Negated Flag",fbold12));
		            c12.setBackgroundColor(new BaseColor(153, 0, 51));
					c12.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					
					Font fbold15 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
		            PdfPCell c15 = new PdfPCell(new Phrase("Negation Flag Date",fbold15));
		            c15.setBackgroundColor(new BaseColor(153, 0, 51));
					c15.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 10:");  
					
					
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 14:");  
					try
					{
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append:");  
						//pdf.addCell(h1);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 1:");  
			            pdf.addCell(c1);
			            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 2:");  
			            pdf.addCell(c2);
			            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 3 :");  
			            pdf.addCell(c3);
			            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 4:");  
			            pdf.addCell(c4);
			            PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 5:");  
						pdf.addCell(c5);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 13:");  
			            pdf.addCell(c13);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 6:");  
			            pdf.addCell(c6);
			            /*PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 7:");  
						pdf.addCell(c7);
						*/
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 8:");  
						pdf.addCell(c8);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 10:");  
						pdf.addCell(c14);
						//PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 9:"); 
						//pdf.addCell(c9);	
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 9:");  
						pdf.addCell(c12);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 12"
								+ ":");  
						pdf.addCell(c15);
						
					}
					catch(Exception e)
					{
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", In catch After image : "+e.getStackTrace());
					}
					
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 10:");
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", CIF_ID.size() "+PCIntegration.BlacklistGridCIFID.size());
					for (int j = 0; j < PCIntegration.BlacklistGridCIFID.size(); j++) {
						
		                c1 = new PdfPCell(new Phrase(PCIntegration.BlacklistGridCIFID.get(j)));
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", Aftr CIFIDarray for WINAME "+WINAME+" : "+PCIntegration.BlacklistGridCIFID.get(j));
						c1.setBackgroundColor(new BaseColor(255,251,240));
						c1.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		                pdf.addCell(c1);
		                
		                c2 = new PdfPCell(new Phrase(PCIntegration.BlacklistGridCifStatus.get(j)));
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", Aftr CIF Statusarray for WINAME "+WINAME+" : "+PCIntegration.BlacklistGridCifStatus.get(j));
						c2.setBackgroundColor(new BaseColor(255,251,240));
						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		                pdf.addCell(c2);
		                
		                c3 = new PdfPCell(new Phrase(PCIntegration.BlacklistGridFullName.get(j)));
		                PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", Aftr fullNamearray for WINAME "+WINAME+" : "+PCIntegration.BlacklistGridFullName.get(j));
						c3.setBackgroundColor(new BaseColor(255,251,240));
						c3.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		                pdf.addCell(c3);
						
						try {
							c4 = new PdfPCell(new Phrase(PCIntegration.BlacklistGridEmiratesID.get(j)));
							//System.out.println("Aftr EmiratesIDarray");
							PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n EmiratesIDarray value for WINAME "+WINAME+" : "+PCIntegration.BlacklistGridEmiratesID.get(j));
							c4.setBackgroundColor(new BaseColor(255,251,240));
							c4.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
							pdf.addCell(c4);
						} 
						catch (Exception e){
							
						}
						
						c5 = new PdfPCell(new Phrase(PCIntegration.BlacklistGridPassportNo.get(j)));
		                //System.out.println("Aftr PassportNumberarray");
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n PassportNumberarray value for WINAME "+WINAME+" : "+PCIntegration.BlacklistGridPassportNo.get(j));
						c5.setBackgroundColor(new BaseColor(255,251,240));
						c5.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		                pdf.addCell(c5);
		                
		                c13 = new PdfPCell(new Phrase(PCIntegration.BlacklistGridDOB.get(j)));
		                //System.out.println("Aftr PassportNumberarray");
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n PassportNumberarray value for WINAME "+WINAME+" : "+PCIntegration.BlacklistGridPassportNo.get(j));
						c13.setBackgroundColor(new BaseColor(255,251,240));
						c13.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		                pdf.addCell(c13);
						
		                c6 = new PdfPCell(new Phrase(PCIntegration.BlacklistGridMobNo.get(j)));
		                //System.out.println("aftr Phonearray");
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n Phonearray value for WINAME "+WINAME+" : "+PCIntegration.BlacklistGridMobNo.get(j));
						c6.setBackgroundColor(new BaseColor(255,251,240));
						c6.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		                pdf.addCell(c6);
		                
		                c8 = new PdfPCell(new Phrase(PCIntegration.BlacklistGridBlacklistedFlag.get(j)));
		                PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n BlackListFlagarray value for WINAME "+WINAME+" : "+PCIntegration.BlacklistGridBlacklistedFlag.get(j));
						c8.setBackgroundColor(new BaseColor(255,251,240));
						c8.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		                pdf.addCell(c8);
		                
		                c14 = new PdfPCell(new Phrase(PCIntegration.BlacklistGridBlacklistedDate.get(j)));
		                PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n BlackListFlagarray value for WINAME "+WINAME+" : "+PCIntegration.BlacklistGridBlacklistedFlag.get(j));
		                c14.setBackgroundColor(new BaseColor(255,251,240));
		                c14.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		                pdf.addCell(c14);
		                
		            	
		                c12 = new PdfPCell(new Phrase(PCIntegration.BlacklistGridNegatedFlag.get(j)));
		                PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n NEGATED_FLAGarray value for WINAME "+WINAME+" : "+PCIntegration.BlacklistGridNegatedFlag.get(j));
						c12.setBackgroundColor(new BaseColor(255,251,240));
						c12.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		                pdf.addCell(c12);
		                
		                c15 = new PdfPCell(new Phrase(PCIntegration.BlacklistGridNegatedDate.get(j)));
		                PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n NEGATED_FLAGarray value for WINAME "+WINAME+" : "+PCIntegration.BlacklistGridNegatedFlag.get(j));
		                c15.setBackgroundColor(new BaseColor(255,251,240));
		                c15.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		                pdf.addCell(c15);
		                
		            	 PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n PDF Generated check 1 for WINAME "+WINAME);           
						 //doc.add(pdf);
		            }
	            }
	            doc.add(pdf);								
	            doc.close();
	            
				PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n PDF Generated Successfully in target location for WINAME "+WINAME);
				String Req_Type="Black_List_Check_Result"; 
				PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n before AttachDocumentWithWI ");
				String Response=CommonMethods.AttachDocumentWithWI(CommonConnection.getCabinetName(),WINAME, CommonConnection.getSessionID(PCLog.PCLogger, false),CommonConnection.getJTSIP(),Integer.parseInt(CommonConnection.getsSMSPort()),generatedPdfPath,DocName);
				PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n Response : "+Response);				
				//***********DeleteFile*************
				String strStatus = CommonMethods.DeleteFile(generatedPdfPath);
				PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n strStatus of deleting the file in blacklist : "+strStatus);
				//**************************************
				RetValue = Response;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				//sMappOutPutXML="Exception"+e;
				PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", in catch of BlackListGeneratePDF Exception is: "+e);

				RetValue = "in catch of BlackListGeneratePDF Exception is: "+e;
			}
		}
		else if(IntegrationCall.equals("COMPLIANCE_CHECK"))
		{
			PCLog.PCLogger.debug("inside Fircosoft PDF");
			//PDF Generation****************************************
			try{
				String Xmlout="";

				String CustName = "";
				String Inputcustdob = "";
				String Inputcustnationality = "";							
				String CIF_NUMBER = "";							
				String EXPIRYDATE = "";						
				String GENDER = "";							
				String RESIDENCEADDRCOUNTRY = "";							
				String PASSPORT_NUMBER = "";
				
				String ReqBusinessUnit = "CENTRALOPERATIONSDUBAI";						
				String PassportIssuingCountry = "AE";
				
	        	String CompayFlag = GridDataMap.get("COMPANYFLAG");
				
				if("C".equalsIgnoreCase(CompayFlag))
				{
					CustName = GridDataMap.get("COMPANY_NAME").trim();
					Inputcustdob = GridDataMap.get("DATEOFINCORPORATION");
					RESIDENCEADDRCOUNTRY = GridDataMap.get("COUNTRY");
				}
				
				if("R".equalsIgnoreCase(CompayFlag))
				{
					String FirstName = GridDataMap.get("FIRSTNAME").trim();
					String MiddleName = GridDataMap.get("MIDDLENAME").trim();
					String LastName = GridDataMap.get("LASTNAME").trim();
				
					CustName = FirstName + " " + LastName;
					if(!"".equalsIgnoreCase(MiddleName))
						CustName = FirstName+" "+ MiddleName + " " + LastName;
										
					GENDER = GridDataMap.get("GENDER");
					if((GENDER).equalsIgnoreCase("F"))
						GENDER = "Female";
					if((GENDER).equalsIgnoreCase("M"))
						GENDER = "Male";
					
					Inputcustdob = GridDataMap.get("DATEOFBIRTH");
					RESIDENCEADDRCOUNTRY = GridDataMap.get("COUNTRYOFRESIDENCE");
					PASSPORT_NUMBER = GridDataMap.get("PASSPORTNUMBER");
				}
	        	
	        	Inputcustnationality = GridDataMap.get("NATIONALITY");							
				CIF_NUMBER = GridDataMap.get("CIF");							
																								
				String path = System.getProperty("user.dir");
				String pdfTemplatePath = "";
				String generatedPdfPath = "";
											
				String imgPath = "";
				String generatedimgPath = "";			


				//Reading path from property file
				Properties properties = new Properties();
				properties.load(new FileInputStream(System.getProperty("user.dir")+ System.getProperty("file.separator")+ "ConfigFiles" + System.getProperty("file.separator")+ "PC_Config.properties"));
					
				
					String pdfName ="Wolrd_Check_Result";
						
					String dynamicPdfName =  WINAME+ pdfName + ".pdf";
				
					pdfTemplatePath = path + pdfTemplatePath;//Getting complete path of the pdf tempplate
					generatedPdfPath = properties.getProperty("PC_GENERTATED_PDF_PATH");//Get the loaction of the path where generated template will be saved
					generatedPdfPath += dynamicPdfName;
					generatedPdfPath = path + generatedPdfPath;//Complete path of generated PDF
									
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \nDedup GeneratedPdfPath :" + generatedPdfPath);
											
					FileOutputStream fileOutputStream = new FileOutputStream(generatedPdfPath);
					com.itextpdf.text.Document doc = new com.itextpdf.text.Document(PageSize.A4.rotate());
					PdfWriter writer = PdfWriter.getInstance(doc, fileOutputStream);
					writer.open();
					doc.open();
					Font bold = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
					
					String dynamicimgName = "bank-logo.gif";
					generatedimgPath = properties.getProperty("PC_LoadLogo");
					generatedimgPath += dynamicimgName;
					generatedimgPath = path + generatedimgPath;
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \nDedup generatedimgPath :" + generatedimgPath);								
					
					Paragraph preface = new Paragraph();
					//generatedimgPath=generatedimgPath.replace("/","//");
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \nDedup generatedimgPath aftr replace:" + generatedimgPath);
					Image img = Image.getInstance(generatedimgPath);
					
					img.setAlignment(Image.ALIGN_RIGHT);  
					img.scaleAbsolute(60f, 40f);
					
					preface.add(img);
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After image");
					doc.add(preface);
														
					// Start - Header Fields formatted on 23112020 by Angad - Working format 1
					Font fontRed = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD,new BaseColor(230, 0, 0));
					
					preface = new Paragraph("Customer Details", fontRed);
					preface.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
					doc.add(preface);
					
					preface=new Paragraph("   ",bold);
					preface.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
					doc.add(preface);
					
					
					PdfPTable pdf1 = new PdfPTable(5);
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable:");
					pdf1.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
					int[] columnWidths1 ={15,30,5,15,30};
					pdf1.setWidths(columnWidths1);
				    pdf1.setWidthPercentage(100);
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 1:");
					
					Font fbld11 = new Font(Font.FontFamily.TIMES_ROMAN, 12,Font.NORMAL, BaseColor.BLACK);
					PdfPCell h11 = new PdfPCell(new Phrase("CIF No",fbld11));
					h11.setBackgroundColor(new BaseColor(235, 235, 224));
				    h11.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);													
					PdfPCell c11 = new PdfPCell(new Phrase(CIF_NUMBER,fbld11));
					c11.setBackgroundColor(BaseColor.WHITE);
				    c11.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);													
					PdfPCell space11 = new PdfPCell(new Phrase("  ",fbld11));
					space11.setBackgroundColor(BaseColor.WHITE);
					space11.disableBorderSide(Rectangle.OUT_RIGHT);
					space11.disableBorderSide(Rectangle.OUT_TOP);
					space11.setBorderColor(BaseColor.WHITE);
						
					Font fbld12 = new Font(Font.FontFamily.TIMES_ROMAN, 12,Font.NORMAL, BaseColor.BLACK);
					PdfPCell h12 = new PdfPCell(new Phrase("Name",fbld12));
					h12.setBackgroundColor(new BaseColor(235, 235, 224));
					h12.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
					PdfPCell c12 = new PdfPCell(new Phrase(CustName,fbld12));
					c12.setBackgroundColor(BaseColor.WHITE);
					c12.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
				
					pdf1.addCell(h11);
					pdf1.addCell(c11);
					pdf1.addCell(space11);
					pdf1.addCell(h12);
					pdf1.addCell(c12);
					doc.add(pdf1);
					
					preface=new Paragraph("   ",bold);
					preface.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
					doc.add(preface);
														
					
					PdfPTable pdf11 = new PdfPTable(5);
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable:");
					pdf11.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
					int[] columnWidths11 = {15,30,5,15,30};
					pdf11.setWidths(columnWidths11);
				    pdf11.setWidthPercentage(100);
					Font fbld13 = new Font(Font.FontFamily.TIMES_ROMAN, 12,Font.NORMAL, BaseColor.BLACK);
					PdfPCell h13 = new PdfPCell(new Phrase("Expiry Date",fbld13));
					h13.setBackgroundColor(new BaseColor(235, 235, 224));
					h13.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
					PdfPCell c13 = new PdfPCell(new Phrase(EXPIRYDATE,fbld13));
					c13.setBackgroundColor(BaseColor.WHITE);
					c13.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
					PdfPCell space13 = new PdfPCell(new Phrase("  ",fbld13));
					space13.setBackgroundColor(BaseColor.WHITE);
					space13.disableBorderSide(Rectangle.OUT_RIGHT);
					space13.disableBorderSide(Rectangle.OUT_TOP);
					space13.setBorderColor(BaseColor.WHITE);
					
					Font fbld14 = new Font(Font.FontFamily.TIMES_ROMAN, 12,Font.NORMAL, BaseColor.BLACK);
					PdfPCell h14 = new PdfPCell(new Phrase("Nationality",fbld14));
					h14.setBackgroundColor(new BaseColor(235, 235, 224));
					h14.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
					PdfPCell c14 = new PdfPCell(new Phrase(Inputcustnationality,fbld14));
					c14.setBackgroundColor(BaseColor.WHITE);
					c14.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
					
					pdf11.addCell(h13);
					pdf11.addCell(c13);
					pdf11.addCell(space13);
					pdf11.addCell(h14);
					pdf11.addCell(c14);
					doc.add(pdf11);
					
					preface=new Paragraph("   ",bold);
					preface.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
					doc.add(preface);
					
														
					PdfPTable pdf12 = new PdfPTable(5);
					pdf12.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
					int[] columnWidths12 = {15,30,5,15,30};
					pdf12.setWidths(columnWidths12);
				    pdf12.setWidthPercentage(100);
					Font fbld15 = new Font(Font.FontFamily.TIMES_ROMAN, 12,Font.NORMAL, BaseColor.BLACK);
					PdfPCell h15 = new PdfPCell(new Phrase("Gender",fbld15));
					h15.setBackgroundColor(new BaseColor(235, 235, 224));
					h15.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
					PdfPCell c15 = new PdfPCell(new Phrase(GENDER,fbld15));
					c15.setBackgroundColor(BaseColor.WHITE);
					c15.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
					PdfPCell space15 = new PdfPCell(new Phrase("  ",fbld15));
					space15.setBackgroundColor(BaseColor.WHITE);
					space15.disableBorderSide(Rectangle.OUT_RIGHT);
					space15.disableBorderSide(Rectangle.OUT_TOP);
					space15.setBorderColor(BaseColor.WHITE);
					
					Font fbld16 = new Font(Font.FontFamily.TIMES_ROMAN, 12,Font.NORMAL, BaseColor.BLACK);
					PdfPCell h16 = new PdfPCell(new Phrase("Residence Country",fbld16));
					h16.setBackgroundColor(new BaseColor(235, 235, 224));
					h16.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
					PdfPCell c16 = new PdfPCell(new Phrase(RESIDENCEADDRCOUNTRY,fbld16));
					c16.setBackgroundColor(BaseColor.WHITE);
					c16.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
					pdf12.addCell(h15);
					pdf12.addCell(c15);
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 16:");  
					pdf12.addCell(space15);
					pdf12.addCell(h16);
					pdf12.addCell(c16);
					doc.add(pdf12);
					
					preface=new Paragraph("   ",bold);
					preface.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
					doc.add(preface);
					
					
					PdfPTable pdf13 = new PdfPTable(5);
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable:");
					pdf13.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
					int[] columnWidths13 = {20,25,5,20,25};
					pdf13.setWidths(columnWidths13);
				    pdf13.setWidthPercentage(100);
					Font fbld17 = new Font(Font.FontFamily.TIMES_ROMAN, 12,Font.NORMAL, BaseColor.BLACK);
					PdfPCell h17 = new PdfPCell(new Phrase("Omniflow Reference No",fbld17));
					h17.setBackgroundColor(new BaseColor(235, 235, 224));
					h17.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
					PdfPCell c17 = new PdfPCell(new Phrase(PCIntegration.FircoGridREFERENCENO.get(0),fbld17));
					c17.setBackgroundColor(BaseColor.WHITE);
					c17.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
					PdfPCell space17 = new PdfPCell(new Phrase("  ",fbld17));
					space17.setBackgroundColor(BaseColor.WHITE);
					space17.disableBorderSide(Rectangle.OUT_RIGHT);
					space17.disableBorderSide(Rectangle.OUT_TOP);
					space17.setBorderColor(BaseColor.WHITE);
					
					Font fbld18 = new Font(Font.FontFamily.TIMES_ROMAN, 12,Font.NORMAL, BaseColor.BLACK);
					PdfPCell h18 = new PdfPCell(new Phrase("Passport Issuing Country",fbld18));
					h18.setBackgroundColor(new BaseColor(235, 235, 224));
					h18.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
					PdfPCell c18 = new PdfPCell(new Phrase(PassportIssuingCountry,fbld18));
					c18.setBackgroundColor(BaseColor.WHITE);
					c18.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
					
					pdf13.addCell(h17);
					pdf13.addCell(c17);
					pdf13.addCell(space17);
					pdf13.addCell(h18);
					pdf13.addCell(c18);
					doc.add(pdf13);
					
					preface=new Paragraph("   ",bold);
					preface.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
					doc.add(preface);
					
					PdfPTable pdf2 = new PdfPTable(8);
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable:");
					pdf2.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
					int[] columnWidths2 = {22,13,5,25,16,5,12,12};
					pdf2.setWidths(columnWidths2);
				    pdf2.setWidthPercentage(100);
					Font fbld19 = new Font(Font.FontFamily.TIMES_ROMAN, 12,Font.NORMAL, BaseColor.BLACK);
					PdfPCell h19 = new PdfPCell(new Phrase("Requesting Business Unit",fbld19));
					h19.setBackgroundColor(new BaseColor(235, 235, 224));
					h19.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
					PdfPCell c19 = new PdfPCell(new Phrase(ReqBusinessUnit,fbld19));
					c19.setBackgroundColor(BaseColor.WHITE);
					c19.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
					PdfPCell space19 = new PdfPCell(new Phrase("  ",fbld19));
					space19.setBackgroundColor(BaseColor.WHITE);
				    space19.disableBorderSide(Rectangle.OUT_RIGHT);
				    space19.setBorderColor(BaseColor.WHITE);
					
					Font fbld20 = new Font(Font.FontFamily.TIMES_ROMAN, 12,Font.NORMAL, BaseColor.BLACK);
					PdfPCell h20 = new PdfPCell(new Phrase("Passport/Trading License No",fbld20));
					h20.setBackgroundColor(new BaseColor(235, 235, 224));
					h20.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
					PdfPCell c20 = new PdfPCell(new Phrase(PASSPORT_NUMBER,fbld20));
					c20.setBackgroundColor(BaseColor.WHITE);
					c20.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
					PdfPCell space20 = new PdfPCell(new Phrase("  ",fbld20));
					space20.setBackgroundColor(BaseColor.WHITE);
				    space20.disableBorderSide(Rectangle.OUT_RIGHT);
				    space20.setBorderColor(BaseColor.WHITE);
					
					Font fbld21 = new Font(Font.FontFamily.TIMES_ROMAN, 12,Font.NORMAL, BaseColor.BLACK);
					PdfPCell h21 = new PdfPCell(new Phrase("Date Of Birth",fbld21));
					h21.setBackgroundColor(new BaseColor(235, 235, 224));
					h21.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
					PdfPCell c21 = new PdfPCell(new Phrase(Inputcustdob,fbld21));
					c21.setBackgroundColor(BaseColor.WHITE);
					c21.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
					
																	  
					pdf2.addCell(h19);
					pdf2.addCell(c19);
					pdf2.addCell(space19);
					pdf2.addCell(h20);
					pdf2.addCell(c20);
					pdf2.addCell(space20);
					pdf2.addCell(h21);
					pdf2.addCell(c21);
					doc.add(pdf2);
					
					preface=new Paragraph("   ",bold);
					preface.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
					doc.add(preface);
					
					String StatusDesc = "Pending";
					String Status = "";
					
					PdfPTable pdf3 = new PdfPTable(5);
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable:");
					pdf3.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
					int[] columnWidths3 = {15,30,5,20,30};
					pdf3.setWidths(columnWidths3);
				    pdf3.setWidthPercentage(100);
					Font fbld22 = new Font(Font.FontFamily.TIMES_ROMAN, 12,Font.NORMAL, BaseColor.BLACK);
					PdfPCell h22 = new PdfPCell(new Phrase("Status",fbld22));
					h22.setBackgroundColor(new BaseColor(235, 235, 224));
					h22.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
					PdfPCell c22 = new PdfPCell(new Phrase(Status,fbld22));
					c22.setBackgroundColor(BaseColor.WHITE);
					c22.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
					PdfPCell space22 = new PdfPCell(new Phrase("  ",fbld22));
					space22.setBackgroundColor(BaseColor.WHITE);
			    	space22.disableBorderSide(Rectangle.OUT_RIGHT);
				    space22.disableBorderSide(Rectangle.OUT_TOP);
				    space22.setBorderColor(BaseColor.WHITE);
				    
					Font fbld23 = new Font(Font.FontFamily.TIMES_ROMAN, 12,Font.NORMAL, BaseColor.BLACK);
					PdfPCell h23 = new PdfPCell(new Phrase("Status Description",fbld23));
					h23.setBackgroundColor(new BaseColor(235, 235, 224));
					h23.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
					PdfPCell c23 = new PdfPCell(new Phrase(StatusDesc,fbld23));
					c23.setBackgroundColor(BaseColor.WHITE);
					c23.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
					
					pdf3.addCell(h22);
					pdf3.addCell(c22);
					pdf3.addCell(space22);
					pdf3.addCell(h23);
					pdf3.addCell(c23);
					doc.add(pdf3);	
					
					// End - Header Fields formatted on 29112020 by Sowmya - Working format 2	
					
					
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 6:");
					
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 6:");
					
					preface=new Paragraph("   ",bold);
					preface.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
					doc.add(preface);
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 7:");
					preface=new Paragraph("Alert Details",fontRed);
					preface.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
					doc.add(preface);
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 8:");
					preface=new Paragraph("   ",bold);
					preface.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
					doc.add(preface);
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After preface 9:");
					PdfPTable pdf = new PdfPTable(10);
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable :");
					pdf.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
					//int[] columnWidths = {35,50,50,55,40,55,50,50,50,50,95};
					int[] columnWidths = {8,8,8,8,8,8,8,8,8,25};
					pdf.setWidths(columnWidths);
					
					pdf.setWidthPercentage(100);
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 1:");
					
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 2:");
					
					Font fbold1 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
					PdfPCell c1 = new PdfPCell(new Phrase("OFAC ID",fbold1));
					//System.out.println("Prepared");
					c1.setBackgroundColor(new BaseColor(153, 0, 51));
					c1.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 3:");
					//PdfPCell c1 = new PdfPCell(new Phrase("CIFID"));
					
					Font fbold2 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
					PdfPCell c2 = new PdfPCell(new Phrase("Matching Text",fbold2));
					//System.out.println("Prepared");
					c2.setBackgroundColor(new BaseColor(153, 0, 51));
					c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					//PdfPCell c2 = new PdfPCell(new Phrase("CIFStatus"));
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 4:");
					
					Font fbold3 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
					PdfPCell c3 = new PdfPCell(new Phrase("Name",fbold3));
				   //System.out.println("Prepared");
					c3.setBackgroundColor(new BaseColor(153, 0, 51));
					c3.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					//PdfPCell c3 = new PdfPCell(new Phrase("FullName"));
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 5:");
					
					
					Font fbold4 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
					PdfPCell c4 = new PdfPCell(new Phrase("Origin",fbold4));
					//System.out.println("Prepared");
					c4.setBackgroundColor(new BaseColor(153, 0, 51));
					c4.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					//PdfPCell c4 = new PdfPCell(new Phrase("EmiratesID"));
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 6:");
					
					Font fbold5 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
					PdfPCell c5 = new PdfPCell(new Phrase("Designation",fbold5));
					//System.out.println("Prepared");
					c5.setBackgroundColor(new BaseColor(153, 0, 51));
					c5.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 7:");
					
					Font fbold6 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
					PdfPCell c6 = new PdfPCell(new Phrase("Date Of Birth",fbold6));
					//System.out.println("Prepared");
					c6.setBackgroundColor(new BaseColor(153, 0, 51));
					c6.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					//PdfPCell c6 = new PdfPCell(new Phrase("Phone"));
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 8:");
					
					
					Font fbold7 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
					PdfPCell c7 = new PdfPCell(new Phrase("User Data1",fbold7));
					//System.out.println("Prepared");
					c7.setBackgroundColor(new BaseColor(153, 0, 51));
					c7.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					//PdfPCell c6 = new PdfPCell(new Phrase("BFlag"));
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 9:"); 
					
					Font fbold8 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
					PdfPCell c8 = new PdfPCell(new Phrase("Nationality",fbold8));
					//System.out.println("Prepared");
					c8.setBackgroundColor(new BaseColor(153, 0, 51));
					c8.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					//PdfPCell c6 = new PdfPCell(new Phrase("BFlag"));
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 10:");  
					
					Font fbold9 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
					PdfPCell c9 = new PdfPCell(new Phrase("Passport",fbold9));
					//System.out.println("Prepared");
					c9.setBackgroundColor(new BaseColor(153, 0, 51));
					c9.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					//PdfPCell c6 = new PdfPCell(new Phrase("BFlag"));
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 11:");  
					
					Font fbold10 = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.BOLD,new BaseColor(255,255,255));
					PdfPCell c10 = new PdfPCell(new Phrase("Additional Info",fbold10));
					//System.out.println("Prepared");
					c10.setBackgroundColor(new BaseColor(153, 0, 51));
					c10.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					//PdfPCell c6 = new PdfPCell(new Phrase("BFlag"));
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 12:");  
					
					try
					{
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append:");  
						//pdf.addCell(h1);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 1:");  
						pdf.addCell(c1);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 2:");  
						pdf.addCell(c2);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 3 :");  
						pdf.addCell(c3);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 4:");  
						pdf.addCell(c4);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 5:");  
						pdf.addCell(c5);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 6:");  
						pdf.addCell(c6);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 7:");  
						pdf.addCell(c7);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 8:");  
						pdf.addCell(c8);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 9:");
						pdf.addCell(c9);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 10:");
						pdf.addCell(c10);
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable append 11:");  
					}
					catch(Exception e)
					{
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", In catch After image : "+e.getStackTrace());
					}
					
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", After PdfPTable 10:");
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", CIF_ID.size() "+PCIntegration.FircoGridSRNo.size());
					for (int j = 0; j < PCIntegration.FircoGridSRNo.size(); j++) {
						
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", before adding OFACID:"+PCIntegration.FircoGridOFACID.get(j));
						
						c1 = new PdfPCell(new Phrase(PCIntegration.FircoGridOFACID.get(j)));
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", Aftr OFAC_ID for WINAME "+WINAME+" : "+PCIntegration.FircoGridOFACID.get(j));
						c1.setBackgroundColor(new BaseColor(255,251,240));
						c1.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						pdf.addCell(c1);								
						
						
						c2 = new PdfPCell(new Phrase(PCIntegration.FircoGridMatchingText.get(j)));
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", Aftr MatchingText for WINAME "+WINAME+" : "+PCIntegration.FircoGridMatchingText.get(j));
						c2.setBackgroundColor(new BaseColor(255,251,240));
						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						pdf.addCell(c2);
						
						c3 = new PdfPCell(new Phrase(PCIntegration.FircoGridName.get(j)));
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", Aftr Name for WINAME "+WINAME+" : "+PCIntegration.FircoGridName.get(j));
						c3.setBackgroundColor(new BaseColor(255,251,240));
						c3.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						pdf.addCell(c3);
						
						try {
							c4 = new PdfPCell(new Phrase(PCIntegration.FircoGridOrigin.get(j)));
							//System.out.println("Aftr EmiratesIDarray");
							PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n Origin value for WINAME "+WINAME+" : "+PCIntegration.FircoGridOrigin.get(j));
							c4.setBackgroundColor(new BaseColor(255,251,240));
							c4.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
							pdf.addCell(c4);
						} catch (Exception e){
							
						}
						
						c5 = new PdfPCell(new Phrase(PCIntegration.FircoGridDestination.get(j)));
						//System.out.println("Aftr PassportNumberarray");
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n Designation value for WINAME "+WINAME+" : "+PCIntegration.FircoGridDestination.get(j));
						c5.setBackgroundColor(new BaseColor(255,251,240));
						c5.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						pdf.addCell(c5);
						
						c6 = new PdfPCell(new Phrase(PCIntegration.FircoGridDOB.get(j)));
						//System.out.println("aftr Nationalityarray");
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n Date_Of_Birth value for WINAME "+WINAME+" : "+PCIntegration.FircoGridDOB.get(j));
						c6.setBackgroundColor(new BaseColor(255,251,240));
						c6.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						pdf.addCell(c6);
						
						
						
						c7 = new PdfPCell(new Phrase(PCIntegration.FircoGridUserData1.get(j)));
						//System.out.println("aftr Phonearray");
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n UserData1 value for WINAME "+WINAME+" : "+PCIntegration.FircoGridUserData1.get(j));
						c7.setBackgroundColor(new BaseColor(255,251,240));
						 PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n PDF Generated check for WINAME "+WINAME);
						c7.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						pdf.addCell(c7);
						
						c8 = new PdfPCell(new Phrase(PCIntegration.FircoGridNationality.get(j)));
						//System.out.println("aftr Phonearray");
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n Nationality value for WINAME "+WINAME+" : "+PCIntegration.FircoGridNationality.get(j));
						c8.setBackgroundColor(new BaseColor(255,251,240));
						c8.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						pdf.addCell(c8);
						
						c9 = new PdfPCell(new Phrase(PCIntegration.FircoGridPassport.get(j)));
						//System.out.println("aftr Phonearray");
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n Passport value for WINAME "+WINAME+" : "+PCIntegration.FircoGridPassport.get(j));
						c9.setBackgroundColor(new BaseColor(255,251,240));
						c9.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						pdf.addCell(c9);
						
						c10 = new PdfPCell(new Phrase(PCIntegration.FircoGridAdditionalInfo.get(j)));
						//System.out.println("aftr Phonearray");
						PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n AdditionalInfo value for WINAME "+WINAME+" : "+PCIntegration.FircoGridAdditionalInfo.get(j));
						c10.setBackgroundColor(new BaseColor(255,251,240));
						c10.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						pdf.addCell(c10);	
						
						 PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n PDF Generated check 1 for WINAME "+WINAME);           
						 //doc.add(pdf);
					}
					
					doc.add(pdf);								
					doc.close();
					
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n PDF Generated Successfully in target location for WINAME "+WINAME);
					String Response=CommonMethods.AttachDocumentWithWI(CommonConnection.getCabinetName(),WINAME, CommonConnection.getSessionID(PCLog.PCLogger, false),CommonConnection.getJTSIP(),Integer.parseInt(CommonConnection.getsSMSPort()),generatedPdfPath,DocName);
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n Response "+Response);
					//***********DeleteFile*************
					String strStatus = CommonMethods.DeleteFile(generatedPdfPath);
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", \n strStatus of deleting the file in firco : "+strStatus);
					//**************************************
					RetValue = Response;
				}
				catch(Exception e)
				{
					e.printStackTrace();
					PCLog.PCLogger.debug("WINAME : "+WINAME+", WSNAME: "+ActivityName+", in catch of FircosoftGeneratePDF Exception is: "+e);
					RetValue = "in catch of FircosoftGeneratePDF Exception is: "+e;
				}
		}
		return RetValue;
	}
	
}
