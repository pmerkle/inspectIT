/**
 *
 */
package rocks.inspectit.server.service.rest;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import rocks.inspectit.server.util.AgentStatusDataProvider;
import rocks.inspectit.shared.all.cmr.model.MethodIdent;
import rocks.inspectit.shared.all.cmr.model.SensorTypeIdent;
import rocks.inspectit.shared.all.exception.BusinessException;
import rocks.inspectit.shared.cs.cmr.service.IGlobalDataAccessService;
import rocks.inspectit.shared.cs.cmr.service.IInvocationDataAccessService;

/**
 * Restful service provider for looking up identifiers ("idents"), such as {@link MethodIdent} or
 * {@link SensorTypeIdent}.
 *
 * @author Philipp Merkle
 *
 */
@Controller
@RequestMapping(value = "/agents/{agentId}/idents")
public class IdentsRestfulService {

	/**
	 * Reference to the existing {@link IGlobalDataAccessService}.
	 */
	@Autowired
	private IGlobalDataAccessService globalDataAccessService;

	/**
	 * Reference to the existing {@link IInvocationDataAccessService}.
	 */
	@Autowired
	private IInvocationDataAccessService invocationDataAccessService;

	/**
	 * {@link AgentStatusDataProvider}.
	 */
	@Autowired
	AgentStatusDataProvider agentStatusProvider;

	@RequestMapping(method = RequestMethod.GET, value = "methods")
	@ResponseBody
	public Set<MethodIdent> getMethodIdents(@PathVariable("agentId") long platformId) throws BusinessException {
		return globalDataAccessService.getCompleteAgent(platformId).getMethodIdents();
	}

	@RequestMapping(method = RequestMethod.GET, value = "sensortypes")
	@ResponseBody
	public Set<SensorTypeIdent> getSensorTypeIdents(@PathVariable("agentId") long platformId) throws BusinessException {
		return globalDataAccessService.getCompleteAgent(platformId).getSensorTypeIdents();
	}

}
