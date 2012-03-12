/**
 * $RCSfile$
 * $Revision: 1710 $
 * $Date: 2005-07-26 11:56:14 -0700 (Tue, 26 Jul 2005) $
 *
 * Copyright (C) 2004-2008 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jivesoftware.openfire.plugin.drawService;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.admin.AuthCheckFilter;
import org.jivesoftware.openfire.plugin.DrawServicePlugin;
import org.jivesoftware.openfire.plugin.openid.OpenIDMananger;

/**
 * Servlet that addition/deletion/modification of the users info in the system.
 * Use the <b>type</b> parameter to specify the type of action. Possible values
 * are <b>add</b>,<b>delete</b> and <b>update</b>.
 * <p>
 * <p/>
 * The request <b>MUST</b> include the <b>secret</b> parameter. This parameter
 * will be used to authenticate the request. If this parameter is missing from
 * the request then an error will be logged and no action will occur.
 * 
 * @author Justin Hunt
 */
public class DrawServiceServlet extends HttpServlet {

	private DrawServicePlugin plugin;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
//		plugin = (DrawServicePlugin) XMPPServer.getInstance().getPluginManager().getPlugin("userservice");

		AuthCheckFilter.addExclude("drawService/drawService");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String type = request.getParameter("type");
			String token_key = request.getParameter("token_key");
			String token_secret = request.getParameter("token_secret");
			
			String openid = OpenIDMananger.getOpenID(OpenIDMananger.TYPE_QQ, token_key, token_secret);
			
			replyMessage(openid, response, response.getWriter());
		} catch (Exception e) {
			replyError(e.getMessage(), response, response.getWriter());
		}
	}

	private void replyMessage(String message, HttpServletResponse response, PrintWriter out) {
		response.setContentType("text/xml");
		out.println("<result>" + message + "</result>");
		out.flush();
	}

	private void replyError(String error, HttpServletResponse response, PrintWriter out) {
		response.setContentType("text/xml");
		out.println("<error>" + error + "</error>");
		out.flush();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	@Override
	public void destroy() {
		super.destroy();
		AuthCheckFilter.removeExclude("drawService/drawService");
	}
}
