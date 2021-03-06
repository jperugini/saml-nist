package gov.nist.toolkit.wsseTool.signature;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nist.hit.ds.wsseTool.api.config.ContextFactory;
import gov.nist.hit.ds.wsseTool.api.config.GenContext;
import gov.nist.hit.ds.wsseTool.api.config.KeystoreAccess;
import gov.nist.hit.ds.wsseTool.api.exceptions.GenerationException;
import gov.nist.hit.ds.wsseTool.generation.opensaml.OpenSamlFacade;
import gov.nist.hit.ds.wsseTool.generation.opensaml.OpenSamlWsseSecurityGenerator;
import gov.nist.hit.ds.wsseTool.parsing.opensaml.OpenSamlSecurityHeader;
import gov.nist.hit.ds.wsseTool.signature.api.Verifier;
import gov.nist.hit.ds.wsseTool.util.MyXmlUtils;
import gov.nist.toolkit.wsseTool.BaseTest;

import java.io.IOException;
import java.security.KeyException;
import java.security.KeyStoreException;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class VerifierTest extends BaseTest {
	
		GenContext context;
		
		private static Logger log = Logger.getLogger(Verifier.class);

		@Before
		public void loadKeystore() throws KeyStoreException {
			String store = "src/test/resources/keystore/keystore";
			String sPass = "changeit";
			String alias = "hit-testing.nist.gov";
			String kPass = "changeit";
			context = ContextFactory.getInstance();
			context.setKeystore(new KeystoreAccess(store,sPass,alias,kPass));
			context.getParams().put("patientId", "D123401^^^&1.1&ISO");
		}

		/*
		 * Make sure we can sign an element. We use an empty element
		 */
		@Test
		public void testVerifyTimeStampSignatureWithDOM() throws GenerationException, KeyException, MarshalException, XMLSignatureException{
			OpenSamlWsseSecurityGenerator wsse = new OpenSamlWsseSecurityGenerator();
			Document doc = wsse .generateWsseHeader(context);
			
			log.debug("header to validate : \n {}" + MyXmlUtils.DomToString(doc) );
			
			OpenSamlFacade saml = new OpenSamlFacade();
			
			OpenSamlSecurityHeader header = new OpenSamlSecurityHeader(saml, doc.getDocumentElement());
			
			Boolean isValid = new Verifier().verifyTimestampSignatureWithDOM(header);
			
			assertTrue(isValid);
		}
		
		/*
		 * Make sure we can sign an element. We use an empty element
		 */
		@Test
		public void testVerifyTimeStampSignatureWithOpenSaml() throws GenerationException, KeyException, MarshalException, XMLSignatureException{
			OpenSamlWsseSecurityGenerator wsse = new OpenSamlWsseSecurityGenerator();
			Document doc = wsse .generateWsseHeader(context);
			
			log.debug("header to validate : \n {}" + MyXmlUtils.DomToString(doc) );
			
			OpenSamlFacade saml = new OpenSamlFacade();
			
			OpenSamlSecurityHeader header = new OpenSamlSecurityHeader(saml, doc.getDocumentElement());
			
			Boolean isValid = new Verifier().verifyTimestampSignature(header);
			
			assertTrue(isValid);
		}
		

		@Test
		public void testVerifyTimestampSignatureGood() throws GenerationException, KeyException, MarshalException, XMLSignatureException, SAXException, IOException, ParserConfigurationException{
			
			Document doc = MyXmlUtils.getDocumentAsResource("signature/validHeader.xml");
			
			OpenSamlFacade saml = new OpenSamlFacade();
			
			OpenSamlSecurityHeader header = new OpenSamlSecurityHeader(saml, doc.getDocumentElement());
			
			Boolean isValid = new Verifier().verifyTimestampSignature(header);
			
			assertTrue(isValid);
		}
		
		@Test
		public void testVerifyTimestampSignatureBad() throws GenerationException, KeyException, MarshalException, XMLSignatureException, SAXException, IOException, ParserConfigurationException{
			
			Document doc = MyXmlUtils.getDocumentAsResource("signature/invalidHeaderTimestampTampered.xml");
			
			OpenSamlFacade saml = new OpenSamlFacade();
			
			OpenSamlSecurityHeader header = new OpenSamlSecurityHeader(saml, doc.getDocumentElement());
			
			Boolean isValid = new Verifier().verifyTimestampSignature(header);
			
			assertFalse(isValid);
		}
		
		//should pass because we only check the timestamp validity
		@Test
		public void testVerifyTimestampSignatureGoodButDocumentBad() throws GenerationException, KeyException, MarshalException, XMLSignatureException, SAXException, IOException, ParserConfigurationException{
			
			Document doc = MyXmlUtils.getDocumentAsResource("signature/invalidHeaderSamlAssertionTampered.xml");
			
			OpenSamlFacade saml = new OpenSamlFacade();
			
			OpenSamlSecurityHeader header = new OpenSamlSecurityHeader(saml, doc.getDocumentElement());
			
			Boolean isValid = new Verifier().verifyTimestampSignature(header);
			
			assertTrue(isValid);
		}

		@Test
		public void testVerifySamlAssertionSignatureBad() throws GenerationException, KeyException, MarshalException, XMLSignatureException, SAXException, IOException, ParserConfigurationException{
			
			Document doc = MyXmlUtils.getDocumentAsResource("signature/invalidHeaderSamlAssertionTampered.xml");
			
			OpenSamlFacade saml = new OpenSamlFacade();
			
			OpenSamlSecurityHeader header = new OpenSamlSecurityHeader(saml, doc.getDocumentElement());
			
			Boolean isValid = new Verifier().verifySamlAssertionSignature(header);
			
			assertFalse(isValid);
		}
}
