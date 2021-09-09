package in.co.eko.eko;

public class CallbackUrlCustomParams{
    private String param1;
    private String param2;

    public void setParam1(String param1){
        this.param1 = param1;
    }

    public String getParam1(){
        return param1;
    }

    public void setParam2(String param2){
        this.param2 = param2;
    }

    public String getParam2(){
        return param2;
    }

    @Override
    public String toString(){
        return
                "CallbackUrlCustomParams{" +
                        "param1 = '" + param1 + '\'' +
                        ",param2 = '" + param2 + '\'' +
                        "}";
    }
}
