package NettyTest;

public class HtmlPage {
	
	private StringBuilder html=new StringBuilder();
	
	public HtmlPage(){
		html.append("<!DOCTIPE html>");
		html.append("<head>");
		html.append("<meta charset=\"utf-8\">");
		
	}
	public HtmlPage setTitle(String title){
		html.append("<title>").append(title).append("<\title>");
		return this;
	}
	public HtmlPage setH1(String head){
		html.append("</head>");
		html.append("<body>");
		html.append("<h1>").append(head).append("</h1>");
		return this;
	}
	public HtmlPage setH2(String head){
		html.append("<h2>").append(head).append("</h2>");
		return this;
	}
	public HtmlPage setParagraph(String paragraph){
		html.append("<p>").append(paragraph).append("</p>");
		return this;
	}
	public HtmlPage openParagraph() {
		html.append("<p>");
		return this;
		
	}
	public HtmlPage setBold(String text){
		html.append("<b>").append(text).append("</b>");
		return this;
	}
	public HtmlPage setText(String text){
		html.append(text);
		return this;
	}
	public HtmlPage closeParagraph(){
		html.append("</p>");
		return this;
	}
	
	
	
	
	
	public String getHtml(){
		html.append("</body>");
		return html.toString();
	}

}
