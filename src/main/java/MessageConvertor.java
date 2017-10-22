import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import javax.json.Json;
import javax.json.JsonObject;

public class MessageConvertor {
    //1. get the loan request
    //2. convert it to json
    //3. reorder json and add wrapper
    //4. convert to xml

    public String processMessage(String loanRequest) throws JSONException {

        //{"ssn":"123456-6543","creditScore":647,"loanAmount":1234567.0,"loanDuration":"6"}'

        //loanRequest = "{\"ssn\":\"123456-6543\",\"creditScore\":647,\"loanAmount\":1234567.0,\"loanDuration\":\"6\"}'";

        JSONObject loan_json = new JSONObject(loanRequest);
        //strip it from the dash and make it an integer
        int ssn = Integer.parseInt(loan_json.getString("ssn").replace("-",""));
        System.out.println("SSN stripped: " + ssn);

        Double loanAmount = loan_json.getDouble("loanAmount");
        //make it start from 1/1/1970

        String loanDuration = loan_json.getString("loanDuration");

        DateConverter dc = new DateConverter();
        dc.diff_from_epoch(Integer.parseInt(loanDuration));

        int creditScore = loan_json.getInt("creditScore");
        //add LoanRequest rapper so you have the correct format
        JsonObject message = Json.createObjectBuilder()
                .add("LoanRequest", Json.createObjectBuilder()
                        .add("ssn", ssn)
                        .add("creditScore", creditScore)
                        .add("loanAmount", loanAmount)
                        .add("loanDuration", dc.diff_from_epoch(Integer.parseInt(loanDuration))))
                .build();

        //<LoanRequest><loanDuration>6</loanDuration><creditScore>453</creditScore><loanAmount>1234567.0</loanAmount><ssn>123456-6543</ssn></LoanRequest>

        System.out.println("Message processed: " + message);

        String messageToSend = message.toString();

        System.out.println(messageToSend);
        JSONObject json = new JSONObject(messageToSend);
        String xml = "";
        try {
            //todo: this shit returns the xml doc in the wrong order
            xml = XML.toString(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            System.out.println("In XML format: " + xml);
        }

        return xml;
    }

}
