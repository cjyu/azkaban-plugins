/*
 * Copyright 2012 LinkedIn Corp.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package azkaban.triggertype.HdfsDataTrigger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.fs.Path;
import org.joda.time.DateTime;
import org.joda.time.ReadablePeriod;

import azkaban.trigger.TriggerAction;
import azkaban.triggertype.HdfsDataTrigger.HdfsDataChecker.PathVariable;
import azkaban.utils.Pair;

public class HdfsDataTrigger {
	
	private int id = -1;
	private String dataSource;
	private List<String> dependentDataPatterns;
	private String hdfsUser;
	private Map<String, PathVariable> variables;
	private ReadablePeriod timeToExpire;
	
	private List<TriggerAction> actions; 
	private int projectId;
	private String flowName;
	
	private DateTime lastModifyTime;
	private DateTime submitTime;
	private String submitUser;
	private String status;
	
	private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([a-zA-Z_.0-9]+)\\}");
	
	public HdfsDataTrigger(
			int id, 
			String dataSource, 
			List<String> dataPatterns, 
			String hdfsUser,
			Map<String, PathVariable> variables, 
			ReadablePeriod timeToExpire, 
			int projectId,
			String flowName,
			List<TriggerAction> actions,
			DateTime lastModifyTime,
			DateTime submitTime,
			String submitUser,
			String status
			) {
		this.id = id;
		this.dataSource = dataSource;
		this.dependentDataPatterns = dataPatterns;
		this.hdfsUser = hdfsUser;
		this.variables = variables;
		this.timeToExpire = timeToExpire;
		this.actions = actions;
		this.projectId = projectId;
		this.flowName = flowName;
		this.lastModifyTime = lastModifyTime;
		this.submitTime = submitTime;
		this.submitUser = submitUser;
		this.status = status;
	}
	
	public String getHdfsUser() {
		return hdfsUser;
	}

	public void setHdfsUser(String hdfsUser) {
		this.hdfsUser = hdfsUser;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public HdfsDataTrigger(
			String dataSource, 
			List<String> dataPatterns, 
			String hdfsUser,
			Map<String, PathVariable> variables, 
			ReadablePeriod timeToExpire, 
			int projectId,
			String flowName,
			List<TriggerAction> actions,
			DateTime lastModifyTime,
			DateTime submitTime,
			String submitUser,
			String status
			) {
		this.dataSource = dataSource;
		this.dependentDataPatterns = dataPatterns;
		this.hdfsUser = hdfsUser;
		this.variables = variables;
		this.timeToExpire = timeToExpire;
		this.actions = actions;
		this.projectId = projectId;
		this.flowName = flowName;
		this.lastModifyTime = lastModifyTime;
		this.submitTime = submitTime;
		this.submitUser = submitUser;
		this.status = status;
	}
	
	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public List<String> getDependentDataPatterns() {
		return dependentDataPatterns;
	}

	public void setDependentDataPatterns(List<String> dependentDataPatterns) {
		this.dependentDataPatterns = dependentDataPatterns;
	}

	public Map<String, PathVariable> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, PathVariable> variables) {
		this.variables = variables;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getFlowName() {
		return flowName;
	}

	public void setTimeToExpire(ReadablePeriod timeToExpire) {
		this.timeToExpire = timeToExpire;
	}

	public void setLastModifyTime(DateTime lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}

	public void setSubmitTime(DateTime submitTime) {
		this.submitTime = submitTime;
	}

	public void setSubmitUser(String submitUser) {
		this.submitUser = submitUser;
	}

	public DateTime getLastModifyTime() {
		return lastModifyTime;
	}
	
	public DateTime getSubmitTime() {
		return submitTime;
	}
	
	public Pair<Integer, String> getIdPair() {
		return new Pair<Integer, String>(projectId, flowName);
	}
	
	public String getSubmitUser() {
		return submitUser;
	}
	
	public ReadablePeriod getTimeToExpire() {
		return timeToExpire;
	}
	
	public List<TriggerAction> getActions() {
		return actions;
	}

	public String getDescription() {
//		return "Hdfs Data Trigger " + getId();
		StringBuffer msg = new StringBuffer();
		msg.append("DT " + getId() + " ");
		for(String pattern : dependentDataPatterns) {
			msg.append(getPathFromPattern(pattern, variables));
			msg.append(" ");
		}
		msg.append(" for flow " + projectId + "." + flowName);
		
		return msg.toString();
	}
	
	private String getPathFromPattern(String pattern, Map<String, PathVariable> variables) {
		StringBuffer replaced = new StringBuffer();
		String value = pattern;
		Matcher matcher = VARIABLE_PATTERN.matcher(value);
		while (matcher.find()) {
			String variableName = matcher.group(1);

			String replacement = String.valueOf(variables.get(variableName).getValue());
			if(variableName.equals("MONTH") || variableName.equals("DAY") || variableName.equals("HOUR")) {
				if(replacement.length() == 1) {
					replacement = "0"+replacement;
				}
			}
			
			matcher.appendReplacement(replaced, replacement);
			matcher.appendTail(replaced);

			value = replaced.toString();
			replaced = new StringBuffer();
			matcher = VARIABLE_PATTERN.matcher(value);
		}
		matcher.appendTail(replaced);
		
		return replaced.toString();
	}

}
