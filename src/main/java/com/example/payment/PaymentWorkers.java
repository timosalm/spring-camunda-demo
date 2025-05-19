package com.example.payment;

import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import io.camunda.zeebe.spring.common.exception.ZeebeBpmnError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentWorkers {

	private final static Logger log = LoggerFactory.getLogger(PaymentWorkers.class);

	@JobWorker(type = "charge-credit-card")
	public Map<String, Double> chargeCreditCard(@Variable(name = "totalWithTax") Double totalWithTax,
												final JobClient client, final ActivatedJob job) {
		if (totalWithTax <= 0) {
			log.warn("The amount to be charged by credit card is negative");
			throw new ZeebeBpmnError("NEGATIVE_AMOUNT",
					"The amount to be charged by credit card is negative", Map.of("totalWithTax", totalWithTax));
		} else if (totalWithTax <= 10) {
			log.warn("Amount is too low to be charged by credit card");
			client.newFailCommand(job).retries(0)
					.variables(Map.of("totalWithTax", totalWithTax))
					.errorMessage("Amount is too low to be charged by credit card")
					.send().join();
			return Map.of();
		}


		log.info("Charging credit card: {}", totalWithTax);
		return Map.of("amountCharged", totalWithTax);
	}
}
