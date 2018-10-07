/**
 *
 */
package rocks.inspectit.server.service.rest;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import rocks.inspectit.server.util.AgentStatusDataProvider;
import rocks.inspectit.shared.all.communication.data.InvocationSequenceData;
import rocks.inspectit.shared.cs.cmr.service.IInvocationDataAccessService;

/**
 * Restful service provider for invocation sequences.
 *
 * @author Philipp Merkle
 *
 */
@Controller
@RequestMapping(value = "/agents/{agentId}/invocations")
public class InvocationSequenceRestfulService {

	@Autowired
	private IInvocationDataAccessService invocationDataAccessService;

	@Autowired
	AgentStatusDataProvider agentStatusProvider;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<Long> getOverview(@PathVariable("agentId") long platformId, @RequestParam(value = "methodId", required = false, defaultValue = "-1") long methodId,
			@RequestParam(value = "sensorTypeId", required = false, defaultValue = "-1") long sensorTypeId) {
		List<InvocationSequenceData> invocationList = null;
		if (methodId != -1) {
			invocationList = invocationDataAccessService.getInvocationSequenceOverview(platformId, methodId, Integer.MAX_VALUE, null);
		} else {
			invocationList = invocationDataAccessService.getInvocationSequenceOverview(platformId, Integer.MAX_VALUE, null);
		}

		Stream<InvocationSequenceData> invocationStream = invocationList.stream();
		if (sensorTypeId != -1) {
			// TODO refine filtering by sensor type
			invocationStream.filter(i -> i.getSensorTypeIdent() == sensorTypeId);
		}
		return invocationStream.map(InvocationSequenceData::getId).collect(Collectors.toList());
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	@ResponseBody
	public InvocationSequenceData getInvocationSequence(@PathVariable("id") long id, @PathVariable("agentId") long platformId,
			@RequestParam(value = "detailed", required = false, defaultValue = "false") boolean detailed) {
		// TODO need comparator?
		List<InvocationSequenceData> templatesList = invocationDataAccessService.getInvocationSequenceOverview(platformId, Collections.singleton(id), Integer.MAX_VALUE, null);
		if (templatesList.isEmpty()) {
			throw new IllegalArgumentException("Could not find invocation sequence for id " + id);
		}
		if (templatesList.size() > 1) {
			throw new IllegalStateException(String.format("Expected exactly one invocation sequence for id %s, but got %s elements.", id, templatesList.size()));
		}
		InvocationSequenceData template = templatesList.get(0);

		if (!detailed) {
			return template;
		} else {
			return invocationDataAccessService.getInvocationSequenceDetail(template);
		}
	}

}
