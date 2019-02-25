package com.sample.jersey.image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.codehaus.jackson.map.ObjectMapper;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/api")
public class ImageApi {

	@Context
	private HttpServletRequest servletRequest;
	@Context
	private HttpServletResponse servletResponse;
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	
	@Path("/test")
	public String test() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		return "Welcome";
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/image/{imageId}")
	public String getImageDetails(@PathParam("imageId") final int imageId) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(InMemoryMockDB.getImage(imageId));
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/image/list")
	public String listImages() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(InMemoryMockDB.list());
	}

	@POST
	@Path("/upload")
	public String uploadFile(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("title") String title, @FormDataParam("extn") String extn) throws Exception {
		OutputStream os = null;
		ImageData imageData = null;
		try {

			if (extn == null) {
				extn = ".png";
			}

			imageData = InMemoryMockDB.addImage(title);
			File fileToUpload = new File(System.getProperty("user.home") + "/" + imageData.getId() + extn);

			imageData.setUrl(getPath(servletRequest) + imageData.getId());

			os = new FileOutputStream(fileToUpload);
			byte[] b = new byte[2048];
			int length;
			while ((length = uploadedInputStream.read(b)) != -1) {
				os.write(b, 0, length);
			}
		} catch (IOException ex) {

		} finally {
			try {
				os.close();
			} catch (IOException ex) {

			}
		}
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(imageData);
	}

	@GET
	@Path("/download/{imageId}")
	@Produces("image/png")
	public Response getFile(@PathParam("imageId") final int imageId) {
		ImageData imageData = InMemoryMockDB.getImage(imageId);
		File fileToDownload = new File(System.getProperty("user.home") + "/" + imageData.getId() + ".png");
		ResponseBuilder response = Response.ok((Object) fileToDownload);
		response.header("Content-Disposition", "attachment; filename="+imageData.getId() +".png");
		return response.build();

	}

	public String getPath(HttpServletRequest request) {
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ "/SampleJerseyApp/rest/api/download/";

	}

}
