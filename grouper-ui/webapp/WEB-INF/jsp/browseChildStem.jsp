<%-- @annotation@
		  Dynamic tile used by all browse modes to render
		  child stems as links
--%><%--
  @author Gary Brown.
  @version $Id: browseChildStem.jsp,v 1.4 2008-04-06 03:45:43 mchyzer Exp $
--%>
<%@include file="/WEB-INF/jsp/include.jsp"%>
<tiles:importAttribute ignore="true"/>
<%-- TODO change tooltip to real tooltip, and from nav.properties --%>
<img  <grouper:tooltip key="stem.icon.tooltip"/> src="grouper/images/folder.gif" class="groupIcon" />&nbsp;<html:link page="/browseStems${browseMode}.do" 
		   paramId="currentNode" 
		   paramName="viewObject" 
		   paramProperty="stemId"
		   title="${navMap['browse.expand.stem']} ${viewObject.displayExtension}">
				<span class="stemView"><c:out value="${viewObject[mediaMap['stem.display']]}"/></span>
</html:link>
