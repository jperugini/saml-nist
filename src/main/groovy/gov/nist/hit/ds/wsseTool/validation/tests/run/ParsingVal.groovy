package gov.nist.hit.ds.wsseTool.validation.tests.run

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*
import static org.junit.Assume.assumeThat;
import gov.nist.hit.ds.wsseTool.namespace.dom.NwhinNamespace;
import gov.nist.hit.ds.wsseTool.validation.engine.ValRunnerWithOrder;
import gov.nist.hit.ds.wsseTool.validation.engine.annotations.*
import gov.nist.hit.ds.wsseTool.validation.tests.BaseVal
import groovy.util.slurpersupport.GPathResult

import java.io.File;
import java.text.MessageFormat

import org.junit.runner.RunWith;


@RunWith(ValRunnerWithOrder.class)
public class ParsingVal extends BaseVal {
	
	@Order(order=1)
	@Validation(id="1019", rtm=["60", "175", "50", "55"],
	description="Check header structure."
	)
	public void timeStamp() {
		assertTrue( "security header must have a timestamp" , !header.map.timestamp.isEmpty())
		String wsu = header.map.timestamp[0].namespaceURI;
		assertEquals( "timestamp has a wrong namespace" , NwhinNamespace.WSU.uri(), wsu)
	}
	
	@Order(order=1)
	@Validation(id="1019", rtm=["60", "175", "50", "55"],
	description="Check header structure."
	)
	public void assertion() {
		assertTrue( "assertion must have an assertion" , !header.map.assertion.isEmpty())
		String saml = header.map.assertion[0].namespaceURI;
		assertEquals( "assertion has a wrong namespace" , NwhinNamespace.SAML2.uri(), saml)
	}
	
	@Order(order=1)
	@Validation(id="1019", rtm=["60", "175", "50", "55"],
	description="Check header structure."
	)
	public void timestampSignature() {
		assertTrue( "security header must have a timestamp signature" , !header.map.t_signature.isEmpty())
		String ds = header.map.t_signature[0].namespaceURI;
		assertEquals( "timestamp signature has a wrong namespace" , NwhinNamespace.DS.uri(), ds)
	}

	@Order(order=2)
	@Validation(id="1024", rtm=[
		"45",
		"48",
		"60",
		"157",
		"165"
	],
	description="check required assertion signature elements are present with a valid cardinality. Check assertion elements ordering."
	)
	public void assertionStructure(){
		def issuers = header.map.assertion.children().findAll{it.name() == 'Issuer'}
		def a_signatures = header.map.assertion.children().findAll{it.name() == 'Signature'}
		def subjects = header.map.assertion.children().findAll{it.name() == 'Subject'}
		def attributeStatements = header.map.assertion.children().findAll{it.name() == 'AttributeStatement'}
		def authnStatements = header.map.assertion.children().findAll{it.name() == 'AuthnStatement'}

		assertEquals( "assertion must have a unique issuer", 1 , issuers.size())
		assertEquals( "assertion must have a unique signature", 1, a_signatures.size())
		assertEquals( "assertion must have a unique subject", 1, subjects.size())
	//	assertEquals( "assertion must have a unique attribute statement", 1, attributeStatements.size()) //can have multiple attribute statements
		assertEquals( "assertion must have a unique authorization statement",1, authnStatements.size())

        /*
        Update on 04-29-2014 : until disproven, we now think that there is no specific ordering for this assertion elements.
        Thus underlying tests are commented out for now.
         */

//		GPathResult children = header.map.assertion.children()
//
//		assertTrue ( MessageFormat.format("assertion first child must be an issuer but was {0}", children[0]), children[0] == header.map.issuer)
//		assertTrue ( MessageFormat.format("assertion second child must be a signature but was {0}", children[1]), children[1] == header.map.a_signature)
//		assertTrue ( MessageFormat.format("assertion third child must be a subject but was {0}", children[2]), children[2] == header.map.subject)

	}

	@Optional
	@Validation(id="1024", rtm=["65"],description="check presence of the optional authz decision statement")
	public void authzDecisionStatementPresence(){
		def authzDecisionStatements = header.map.assertion.children().findAll{it.name() == 'AuthzDecisionStatement'} //optional

		assumeThat(MessageFormat.format("assertion may contain a authz decision statement. Found {0} authz decision statement" , authzDecisionStatements.size()),
				authzDecisionStatements.size(), not(is(0)));

		assertEquals( "if present, authorization decision statement must be unique", 1, authzDecisionStatements.size())

	}

	@Optional
	@Validation(id="1024",description="check presence of optional conditions")
	public void conditionsPresence(){
		def conditions = header.map.assertion.children().findAll{it.name() == 'Conditions'} //optional
		
		assumeThat(MessageFormat.format("assertion may contain conditions. Found {0} conditions" , conditions.size()),
			conditions.size(), not(is(0)));
	}

	@Optional
	@Validation(id="1024", description="check presence of optional advices")
	public void advicePresence(){
		def advices = header.map.assertion.children().findAll{it.name() == 'Advice'} //optional
		
		assumeThat(MessageFormat.format("assertion may contain advices. Found {0} advices" , advices.size()),
			advices.size(), not(is(0)));
	}



	@Order(order=3)
	@Validation(id="1035", rtm=["59"],
	description="check required assertion signature elements are present with a valid cardinality. Check assertion elements ordering.")
	public void a_signatureStructure(){

		def as_signedInfo = header.map.a_signature.children().findAll{it.name() == 'SignedInfo'}
		def as_signatureValue = header.map.a_signature.children().findAll{it.name() == 'SignatureValue'}
		def as_keyInfo = header.map.a_signature.children().findAll{it.name() == 'KeyInfo'}
		def as_objects = header.map.a_signature.children().findAll{it.name() == 'Object'} //optional

		assertTrue( MessageFormat.format("assertion signature must have a unique signedInfo but found {0}",as_signedInfo.size()) , as_signedInfo.size() == 1)
		assertTrue( MessageFormat.format("assertion signature must have a unique signatureValue but found {0}",as_signatureValue.size()) , as_signatureValue.size() == 1)
		assertTrue( MessageFormat.format("assertion signature must have a unique keyInfo but found {0}",as_keyInfo.size()) , as_keyInfo.size() == 1)

		GPathResult children = header.map.a_signature.children()

		assertTrue( MessageFormat.format("assertion signature first child must be a signedInfo but was {0}", children[0]) , children[0] == header.map.as_signedInfo)
		assertTrue( MessageFormat.format("assertion signature second child must be a signatureValue but was {0}", children[1]) , children[1] == header.map.as_signatureValue)
		assertTrue( MessageFormat.format("assertion signature third child must be a keyInfo but was {0}", children[2]) , children[2] == header.map.as_keyInfo)
	}
}
