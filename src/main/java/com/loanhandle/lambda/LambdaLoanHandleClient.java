package com.loanhandle.lambda;

import java.nio.charset.StandardCharsets;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambdaAsyncClient;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.google.gson.Gson;
import com.moneycaptcha.lambda.rest.model.LoanInput;
import com.moneycaptcha.lambda.rest.model.LoanOutput;

public class LambdaLoanHandleClient {
	public static void main(String[] args) {

		String previousBalance = "10000";
		String installment = "1000";
		String valid = "true";
		String region = "us-west-2";
		if (args.length > 0) {
			previousBalance = args[0];
		}
		if (args.length > 1) {
			installment = args[1];
		}
		if (args.length > 2) {
			valid = args[2];
		}
		if (args.length > 3) {
			region = args[3];
		}
		
		Gson gson = new Gson();

		
		AWSLambdaAsyncClient asyncClient = new AWSLambdaAsyncClient(new ProfileCredentialsProvider("loanLambda")).withRegion(Regions.fromName(region));
		
		LoanInput input = new LoanInput();
		input.setPreviousBalance(Float.parseFloat(previousBalance));
		input.setInstallment(Float.parseFloat(installment));
		input.setValidUpdate(Boolean.parseBoolean(valid));
		
		InvokeRequest invokeRequest = new InvokeRequest().withFunctionName("HandleLoan").withPayload(gson.toJson(input));
		InvokeResult result = asyncClient.invoke(invokeRequest);
		
		String outputStr = StandardCharsets.UTF_8.decode(result.getPayload()).toString();
		LoanOutput output = gson.fromJson(outputStr, LoanOutput.class); 
		
		System.out.println("Message: " + output.getMessage());
		System.out.println("Latest Balance: " + output.getUpdatedBalance());
		System.out.println("Function Name: " + output.getFunctionName());
		
	}
}
