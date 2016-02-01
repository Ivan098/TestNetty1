package NettyTest;

import io.netty.buffer.Unpooled;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

import java.security.cert.CertPathValidatorException.Reason;

public class ServerHandler extends SimpleChannelInboundHandler<Object> {
	
	//private String url;
	private HttpRequest request;
	private  final StringBuilder builder=new StringBuilder();
	
	
	
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception{
		super.channelRegistered(ctx);
	}
	
	class UrlMapper{
		private HttpMethod method;
		private String[] path;
		private Map<String,List<String>> params;
		private ChannelHandlerContext ctx;
		
		
		
		
		public UrlMapper(ChannelHandlerContext ctx){
			method=request.getMethod();
			path=request.getUri().replaceFirst("^/", "").split("/");
			path[path.length-1]=path[path.length-1].split("\\?")[0];
			params=new QueryStringDecoder(request.getUri()).parameters();
			this.ctx=ctx;
		}
			public void responseMethod() {
				
				/*if(method!=HttpMethod.GET){
					send501NotImplemented(ctx);
					return;
				}*/
				if(path.length==1){
					if(path[0].equals("hello")&& params.size()==0){
						sendHello(ctx);
						return;
					}
					if(path[0].equals("status")&& params.size()==0){
						sendStatus(ctx);
						return;
					}
					if(path[0].equals("redirect")&& params.size()==1 && params.get("url").size()==1){
						sendRedirect(ctx,params.get("url").get(0));
						return;
					}
				}
				send404(ctx);
			}
			
		}
		
	
	
   
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
    

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;
        	
         

            if (HttpUtil.is100ContinueExpected(req)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
              
            }
            new UrlMapper(ctx).responseMethod();
            
          
            /*boolean keepAlive = HttpUtil.isKeepAlive(req);
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(CONTENT));
            
            response.headers().set(CONTENT_TYPE, "text/plain");
            response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());

            if (!keepAlive) {
                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(CONNECTION, KEEP_ALIVE);
                ctx.write(response);
            }*/
        }
    }
    
    private void writeResponse (HttpObject currentObj, ChannelHandlerContext ctx) {
    	boolean keepAlive = HttpUtil.isKeepAlive(request);
    	FullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, currentObj.getDecoderResult().isSuccess()? HttpResponseStatus.OK :
    		HttpResponseStatus.BAD_REQUEST, Unpooled.copiedBuffer(builder.toString(), CharsetUtil.UTF_8));
    	
    	response.headers().set(HttpHeaders.Names.CONTENT_TYPE,"text/html; charset=utf_8");
    	if (keepAlive) {
            // Add 'Content-Length' header only for a keep-alive connection.
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
            // Add keep alive header as per:
            // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        // Write the response.
        ctx.write(response);

        if (!keepAlive) {
            // If keep-alive is off, close the connection once the content is fully written.
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        } else {
            ctx.flush();
        }
    	
    }
    
    public void sendHello(ChannelHandlerContext ctx){
    	HtmlPage page=new HtmlPage();
    	page.setTitle("Hello ");
    	page.setH1("Hello Word");
    	
    	builder.setLength(0);
    	builder.append(page.getHtml());
    	ctx.executor().schedule(() -> writeResponse(request, ctx), 1, TimeUnit.SECONDS);
    	
    	
    }
    public void sendStatus(ChannelHandlerContext ctx){
    	
    }
    public void sendRedirect(ChannelHandlerContext ctx, String DestinationUrl){
    	
    }
    public void send404(ChannelHandlerContext ctx){
    	
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}