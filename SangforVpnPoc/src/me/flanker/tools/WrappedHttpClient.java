package me.flanker.tools;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class WrappedHttpClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.err.println("orz");
		try {
			System.out.println(new WrappedHttpClient().GetContent("http://www.baidu.com"));
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private DefaultHttpClient client = new DefaultHttpClient();
	{
		X509TrustManager xtm = new X509TrustManager(){   //创建TrustManager 
            public void checkClientTrusted(X509Certificate[] chain, String authType) {} 
            public void checkServerTrusted(X509Certificate[] chain, String authType) {} 
            public X509Certificate[] getAcceptedIssuers() { return null; } 
        }; 
        try { 
            //TLS1.0与SSL3.0基本上没有太大的差别，可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext 
            SSLContext ctx = SSLContext.getInstance("TLS"); 
             
            //使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用 
            ctx.init(null, new TrustManager[]{xtm}, null); 
             
            //创建SSLSocketFactory 
            SSLSocketFactory sf = new SSLSocketFactory(
            	    ctx,
            	    SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            //通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上 
            client.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, sf)); 
        }
        catch (Exception e) {
			e.printStackTrace();
		}
		/*client.setRedirectStrategy(new DefaultRedirectStrategy(){ 
			
		    @Override
			public boolean isRedirected(HttpRequest request,
					HttpResponse response, HttpContext context)
					throws ProtocolException {
		    	boolean isRedirect = super.isRedirected(request,response, context);
		        if (!isRedirect) {
		            int responseCode = response.getStatusLine().getStatusCode();
		            if (responseCode == 301 || responseCode == 302) {
		                return true;
		            }
		        }
		        return isRedirect;
			}

		});
		*/
		/*
		 * debugging purpose DefaultHttpClient httpclient = new
		 * DefaultHttpClient(); HttpHost proxy = new HttpHost("175.159.120.254",
		 * 80);
		 * httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
		 * proxy);
		 */
	}
	private List<NameValuePair> nvps = new ArrayList<NameValuePair>();

	private String encoding = "utf-8";

	private final String TAG = "WrappedHttpClient";

	public void AddToPostContent(String id, String cnt) {
		nvps.add(new BasicNameValuePair(id, cnt));
	}

	public void cleanPostContent() {
		nvps.clear();
	}

	public ArrayList<Byte> DownloadByteArray(String url) throws Exception {
		HttpGet httpGet = new HttpGet(url);
		ArrayList<Byte> a = new ArrayList<Byte>();
		HttpResponse response = client.execute(httpGet);

		if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {

				InputStream input = entity.getContent();
				byte b[] = new byte[1024];
				int j = 0;
				while ((j = input.read(b)) != -1) {
					for (int i = 0; i < j; i++)
						a.add(b[i]);
				}
				//if (entity != null) {
				//	EntityUtils.consume(entity);
				//}
			}
		}
		return a;
	}

	public String GetContent(String url) throws ClientProtocolException,
			IOException {
		HttpResponse response = null;
		HttpGet httpget = new HttpGet(url);
		response = client.execute(httpget);
		return ResponseToString(response);
	}

	public String getEncoding() {
		return encoding;
	}

	public String PostContent(String url) throws ClientProtocolException,
			IOException {
		HttpPost httpost = new HttpPost(url);
		httpost.setEntity(new UrlEncodedFormEntity(nvps, encoding));
		HttpResponse response = client.execute(httpost);
		return ResponseToString(response);
	}

	public String ResponseToString(HttpResponse response) throws ParseException, IOException {
		String responseBody = null;
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			responseBody = EntityUtils.toString(entity);
		}
		//if (entity != null)
		//	EntityUtils.consume(entity);
		return responseBody;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}
