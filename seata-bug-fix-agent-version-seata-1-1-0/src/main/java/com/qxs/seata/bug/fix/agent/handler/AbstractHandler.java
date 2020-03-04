package com.qxs.seata.bug.fix.agent.handler;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractHandler implements IHandler{

    protected static String findCode(Document document, String path){
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            Node node = ((NodeList) xPath.evaluate(path, document, XPathConstants.NODESET)).item(0);
            return node.getTextContent();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected static Document createDocument(InputStream inputStream){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            return document;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
