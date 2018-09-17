<%@ include file="/WEB-INF/jsp/init.jsp" %>
<portlet:resourceURL id="showReport" var="showReport" ></portlet:resourceURL>
<p>
	<b><liferay-ui:message key="PosadaReporte.caption"/></b>
</p>
<div class="row campos">
<div class="col-md-11 column">
<h4>Reporte Posadas</h4>

</div>
<iframe id="card" width="750px" height="400px" style="padding: 0; margin: 0; border: none;"
src='<%=showReport%>'></iframe>
</div>