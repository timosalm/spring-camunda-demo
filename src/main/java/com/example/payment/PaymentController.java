package com.example.payment;

import io.camunda.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PaymentController {
	private final static Logger log = LoggerFactory.getLogger(PaymentController.class);

	private final ZeebeClient zeebeClient;

	public PaymentController(ZeebeClient zeebeClient) {
		this.zeebeClient = zeebeClient;
	}

	@PostMapping("/payment")
	ResponseEntity<Void> processPayment(@RequestBody PaymentRequest request) {
		var event = this.zeebeClient.newCreateInstanceCommand()
				.bpmnProcessId("process-payments")
				.latestVersion()
				.variables(Map.of("total", request.total()))
				.send()
				.join();
		log.info("Started a process instance: {}", event.getProcessInstanceKey());
		return ResponseEntity.ok().build();
	}
}
