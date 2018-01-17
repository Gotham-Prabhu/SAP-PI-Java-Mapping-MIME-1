# Create Email-MIME body with base64 encoded csv attachments

    ECC -> PI -> Email to client

1.	Incoming XML document containing the base64 encoded CSV files (Structure of the xsd is predecided).
2.	Read the xml document from the inputstream using Document Builder, extract the base64 encoded csv files.
3.	Create MIME document for the mail using string builder.
4.	Attach the base64 encoded CSV strings into the mail with each MIME part separated by the boundary.
5.  Write the MIME part of the mail to the outputstream.
6.  Write the content-type to the Transformation Output header.




