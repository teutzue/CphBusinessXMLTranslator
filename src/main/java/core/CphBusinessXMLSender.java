package core;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class CphBusinessXMLSender {

    //1.figure out how to send the message at the bank's specified address and exchange
    //2. send the message and set the reply que and correlationId :)

    private Connection connection;
    private Channel channel;
    private String requestQueueName = "rpc_queue_xml";
    private String replyQueueName;

    public CphBusinessXMLSender() throws IOException, TimeoutException, NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri("amqp://student:cph@datdb.cphbusiness.dk:5672");

        connection = factory.newConnection();
        channel = connection.createChannel();

        replyQueueName = channel.queueDeclare().getQueue();;
    }

    //method that sends the message to the bank and waits for the response.
    public String call(String message) throws IOException, InterruptedException {
        final String corrId = UUID.randomUUID().toString();

        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("cphbusiness.bankXML", requestQueueName, props, message.getBytes("UTF-8"));

        final BlockingQueue<String> response = new ArrayBlockingQueue<String>(1);

        channel.basicConsume(replyQueueName, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                if (properties.getCorrelationId().equals(corrId)) {
                    response.offer(new String(body, "UTF-8"));
                }
            }
        });

        return response.take();
    }

    public void close() throws IOException {
        connection.close();
    }


    public void sendToBank(String message) throws ParserConfigurationException, SAXException {
        CphBusinessXMLSender cphBuisnessXml = null;
        String response = null;
        try {
            cphBuisnessXml = new CphBusinessXMLSender();

            System.out.println(" [x] Requesting response from CPHBusiness XML bank.");
            //here insert the message generated in XML and call this in main

            //System.out.println("Sending the following XML: " + message);
            response = cphBuisnessXml.call(message);
            /*response = cphBuisnessXml.call(" <LoanRequest><loanDuration>1976-01-01 18:35:12.0 CET</loanDuration>" +
                    "<creditScore>525</creditScore><loanAmount>1234567.0</loanAmount><ssn>1234566543</ssn></LoanRequest>");*/
            System.out.println(" [.] Got response back '" + response + "'");

            //add the origin bank to the resp
            response = addBankName(response,"<bank>NordeaBank</bank>");

            //send the response to the normalizer :)
            NormalizerSender nz = new NormalizerSender();
            nz.sendToNormalizer(response);


        }
        catch  (IOException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            if (cphBuisnessXml!= null) {
                try {
                    cphBuisnessXml.close();
                }
                catch (IOException _ignore) {}
            }
        }
    }

    public String addBankName(String message, String append) {
        return message.substring(0, 14) + append+message.substring(14, message.length());
    }
}
