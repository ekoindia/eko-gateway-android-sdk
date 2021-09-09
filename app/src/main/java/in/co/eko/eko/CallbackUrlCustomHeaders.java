package in.co.eko.eko;

public class CallbackUrlCustomHeaders{
    private String header2;
    private String header1;

    public void setHeader2(String header2){
        this.header2 = header2;
    }

    public String getHeader2(){
        return header2;
    }

    public void setHeader1(String header1){
        this.header1 = header1;
    }

    public String getHeader1(){
        return header1;
    }

    @Override
    public String toString(){
        return
                "CallbackUrlCustomHeaders{" +
                        "header2 = '" + header2 + '\'' +
                        ",header1 = '" + header1 + '\'' +
                        "}";
    }
}