<%@ page import="javax.servlet.http.HttpUtils" %>
<%@ page import="java.util.*" %>

<% out.println("########################  BEGIN Request Debugging #########################"); %>

<hr>
<h3>Requested URL</h3>
<pre class="autoOverflow">
<%= HttpUtils.getRequestURL(request) %>
</pre>

<h3>Request Parameters</h3>
<pre class="autoOverflow">
<%
Enumeration enumeration = request.getParameterNames();
while(enumeration.hasMoreElements()){
  String key = (String)enumeration.nextElement();
  String[] paramValues = request.getParameterValues(key);
  for(int i=0;i < paramValues.length;i++){
      out.println(key + " : "  + paramValues[i]); 
  }
}
%>
</pre>

<h3>Request Attributes</h3>
<pre class="autoOverflow">
<%
enumeration = request.getAttributeNames();
while(enumeration.hasMoreElements()){
  String key = (String)enumeration.nextElement();
  String value = request.getAttribute(key).toString();
  String klass = ((Object)request.getAttribute(key)).getClass().getName();
  out.println(key + " : " + klass + " : "  + value + " | "); 
}

%>
</pre>

<h3>Request Information</h3>
<pre class="autoOverflow">
Request Method: <%= request.getMethod() %>
Request URI: <%= request.getRequestURI() %>
Request Protocol: <%= request.getProtocol() %>
Servlet Path: <%= request.getServletPath() %>
Path Info: <%= request.getPathInfo() %>
Path Translated: <%= request.getPathTranslated() %>
Query String: <%= request.getQueryString() %>
Content Length: <%= request.getContentLength() %>
Content Type: <%= request.getContentType() %>
Server Name: <%= request.getServerName() %>
Server Port: <%= request.getServerPort() %>
Remote User: <%= request.getRemoteUser() %>
Remote Address: <%= request.getRemoteAddr() %>
Remote Host: <%= request.getRemoteHost() %>
Authorization Scheme: <%= request.getAuthType() %>
</pre>

<h3>Request Headers</h3>
<pre class="autoOverflow">
<%
enumeration = request.getHeaderNames();
while (enumeration.hasMoreElements()) {
  String name = (String)enumeration.nextElement();
  out.println(name + ": " + request.getHeader(name));
}
%>
</pre>	
<% out.println("########################  END Request Debugging #########################"); %>
