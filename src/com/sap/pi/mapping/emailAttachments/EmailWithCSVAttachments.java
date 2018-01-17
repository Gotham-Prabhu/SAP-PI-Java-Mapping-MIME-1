package com.sap.pi.mapping.emailAttachments;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sap.aii.mapping.api.AbstractTransformation;
import com.sap.aii.mapping.api.StreamTransformationException;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;

/**
 * Testing Purpose
 */
// import java.io.File;
// import java.io.FileFilter;
// import java.io.FileNotFoundException;
//
// import java.nio.file.Files;
// import java.nio.file.Paths;
// import java.nio.file.Path;
//
// import java.util.Base64;

public class EmailWithCSVAttachments extends AbstractTransformation {

	StringBuilder emailBuilder = new StringBuilder();
	String CRLF = "\r\n";
	String boundary = "001a114bc6f2d60884056265dfdc";
	String contentType = "multipart/mixed; boundary=\"" + boundary + "\"";
	String mailContent = "Dear User," + CRLF + "PFA the required CSV files for the user" + CRLF + CRLF + CRLF
			+ "Regards," + CRLF + "SAP PI team";

	Map<String, String> encodedFiles = new HashMap<String, String>();

	/**
	 * Testing Purpose
	 */
	// public static void main(String[] args) throws IOException {
	//
	// }

	@Override
	public void transform(TransformationInput arg0, TransformationOutput arg1) throws StreamTransformationException {
		InputStream inputStream = arg0.getInputPayload().getInputStream();
		OutputStream outputStream = arg1.getOutputPayload().getOutputStream();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		getTrace().addInfo("parsing inputstream and extracting base64 Encoded csv files");
		try {

			DocumentBuilder documentBuilder = factory.newDocumentBuilder();
			Document document = documentBuilder.parse(inputStream);
			NodeList uploadList = document.getElementsByTagName("Upload");
			for (int nodeCount = 0; nodeCount < uploadList.getLength(); nodeCount++) {
				Node uploadNode = uploadList.item(nodeCount);
				if (uploadNode.getNodeType() == Node.ELEMENT_NODE) {
					Element uploadElement = (Element) uploadNode;
					encodedFiles.put(uploadElement.getElementsByTagName("Filename").item(0).getTextContent(),
							uploadElement.getElementsByTagName("Base64EncodedContent").item(0).getTextContent());
				}
			}

			getTrace().addInfo("creating the MIME body for the mail");
			createMIMEBody();

			getTrace().addInfo("wrting the MIME mail body to the outputstream");
			outputStream.write(emailBuilder.toString().getBytes());
			arg1.getOutputHeader().setContentType(contentType);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public StringBuilder createMIMEBody() {
		emailBuilder.append("ContentType: multipart/mixed; boundary=\"" + boundary + "\"" + CRLF + CRLF);
		emailBuilder.append("--" + boundary + CRLF + "Content-Type: text/plain; charset=UTF-8" + CRLF
				+ "Content-Disposition: inline" + CRLF + CRLF + mailContent + CRLF + CRLF);

		for (int fileCount = 0; fileCount < encodedFiles.size(); fileCount++) {

			emailBuilder.append("--" + contentType + CRLF + "Content-Type: text/csv; name=\""
					+ encodedFiles.keySet().toArray()[fileCount] + "\"" + CRLF
					+ "Content-Disposition: attachment; filename=\"" + encodedFiles.keySet().toArray()[fileCount] + "\""
					+ CRLF + "Content-Transfer-Encoding: base64" + CRLF);

			emailBuilder.append(encodedFiles.get(encodedFiles.keySet().toArray()[fileCount]) + CRLF + CRLF);
		}

		emailBuilder.append("--" + contentType + "--" + CRLF);

		return emailBuilder;
	}

	/**
	 * Testing Purpose Function to convert CSV files to Base64 encoded files
	 */

	// private static Map<String, String> convertToBase64 () throws IOException {
	//
	// Map<String,String> filemap = new HashMap<String,String>();
	// File dir = new
	// File("C:/Users/gautamp/eclipse-workspace/EmailWithCSVAttachments/sample_files");
	// for (File file: dir.listFiles()) {
	// Path path = Paths.get(file.getAbsolutePath());
	// byte[] data = Files.readAllBytes(path);
	// String mimeEncoded = Base64.getMimeEncoder().encodeToString(data);
	// filemap.put(path.getFileName().toString(), mimeEncoded);
	// }
	//
	// return filemap;
	//
	// }

}
