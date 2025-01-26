package com.trustwise.restcontroller;

import com.trustwise.bean.EvaluationLog;
import com.trustwise.business.EvaluationOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/evaluation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EvaluationController {

	private static final Logger LOGGER = LoggerFactory.getLogger(EvaluationController.class);
	private static final EvaluationOperations evaluationOps = new EvaluationOperations();

	@POST
	@Path("/log/emotional")
	public Response createEvaluationLogEmotional(String inputText) {
		if (inputText == null || inputText.isEmpty()) {
			LOGGER.error("Input text is null or empty for emotional evaluation log.");
			return Response.status(Response.Status.BAD_REQUEST).entity("Input text cannot be null or empty.").build();
		}
		try {
			EvaluationLog savedLog = evaluationOps.createEvaluationLogEmotional(inputText);
			if (savedLog != null) {
				return Response.ok(savedLog).build();
			} else {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to create emotional evaluation log.").build();
			}
		} catch (Exception e) {
			LOGGER.error("Error creating emotional evaluation log.", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error occurred while creating the emotional evaluation log.").build();
		}
	}

	@POST
	@Path("/log/educ")
	public Response createEvaluationLogEducation(String inputText) {
		if (inputText == null || inputText.isEmpty()) {
			LOGGER.error("Input text is null or empty for educational evaluation log.");
			return Response.status(Response.Status.BAD_REQUEST).entity("Input text cannot be null or empty.").build();
		}
		try {
			EvaluationLog savedLog = evaluationOps.createEvaluationLogEducation(inputText);
			if (savedLog != null) {
				return Response.ok(savedLog).build();
			} else {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to create educational evaluation log.").build();
			}
		} catch (Exception e) {
			LOGGER.error("Error creating educational evaluation log.", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error occurred while creating the educational evaluation log.").build();
		}
	}

	@GET
	@Path("/logs")
	public Response getAllEvaluationLogs() {
		try {
			return Response.ok(evaluationOps.getAllEvaluationLogs()).build();
		} catch (Exception e) {
			LOGGER.error("Error fetching all evaluation logs.", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error occurred while fetching all evaluation logs.").build();
		}
	}

	@GET
	@Path("/log/{id}")
	public Response getEvaluationLogById(@PathParam("id") Long id) {
		try {
			EvaluationLog log = evaluationOps.getEvaluationLogById(id);
			if (log != null) {
				return Response.ok(log).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("Evaluation log not found for ID: " + id).build();
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching evaluation log by ID.", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error occurred while fetching the evaluation log by ID.").build();
		}
	}

	@DELETE
	@Path("/log/{id}")
	public Response deleteEvaluationLog(@PathParam("id") Long id) {
		try {
			evaluationOps.deleteEvaluationLog(id);
			return Response.ok("Evaluation log deleted successfully.").build();
		} catch (Exception e) {
			LOGGER.error("Error deleting evaluation log by ID.", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error occurred while deleting the evaluation log by ID.").build();
		}
	}
}