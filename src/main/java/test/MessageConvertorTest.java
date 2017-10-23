package test;

import static org.junit.Assert.*;

import org.json.JSONException;
import org.junit.Test;

import core.MessageConvertor;

public class MessageConvertorTest {

	@Test
	public void MessageConvertorTest() throws JSONException {
		String loanRequest = "{\"ssn\":\"123456-6543\",\"creditScore\":647,\"loanAmount\":1234567.0,\"loanDuration\":\"6\"}'";
		String expectedResult = ""
				+ "<LoanRequest>"
				+ "<loanDuration>1976-01-01 18:35:12.0 CET</loanDuration>"
				+ "<creditScore>647</creditScore>"
				+ "<loanAmount>1234567.0</loanAmount>"
				+ "<ssn>1234566543</ssn>"
				+ "</LoanRequest>";
		MessageConvertor mc = new MessageConvertor();
		assertEquals(expectedResult, mc.processMessage(loanRequest));
		
	}

}
