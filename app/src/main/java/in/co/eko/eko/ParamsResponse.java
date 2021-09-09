package in.co.eko.eko;

import com.google.gson.annotations.SerializedName;

public class ParamsResponse {

    @SerializedName("callback_url")
    private String callbackUrl;

    @SerializedName("secret_key")
    private String secretKey;

    @SerializedName("user_code")
    private String userCode;

    @SerializedName("initiator_id")
    private String initiatorId;

    @SerializedName("callback_url_custom_headers")
    private CallbackUrlCustomHeaders callbackUrlCustomHeaders;

    @SerializedName("gateway_url")
    private String gatewayUrl;

    @SerializedName("partner_name")
    private String partnerName;

    @SerializedName("secret_key_timestamp")
    private String secretKeyTimestamp;

    @SerializedName("developer_key")
    private String developerKey;

    @SerializedName("initiator_logo_url")
    private String initiatorLogoUrl;

    @SerializedName("language")
    private String language;

    @SerializedName("callback_url_custom_params")
    private CallbackUrlCustomParams callbackUrlCustomParams;

    public void setCallbackUrl(String callbackUrl){
        this.callbackUrl = callbackUrl;
    }

    public String getCallbackUrl(){
        return callbackUrl;
    }

    public void setSecretKey(String secretKey){
        this.secretKey = secretKey;
    }

    public String getSecretKey(){
        return secretKey;
    }

    public void setUserCode(String userCode){
        this.userCode = userCode;
    }

    public String getUserCode(){
        return userCode;
    }

    public void setInitiatorId(String initiatorId){
        this.initiatorId = initiatorId;
    }

    public String getInitiatorId(){
        return initiatorId;
    }

    public void setCallbackUrlCustomHeaders(CallbackUrlCustomHeaders callbackUrlCustomHeaders){
        this.callbackUrlCustomHeaders = callbackUrlCustomHeaders;
    }

    public CallbackUrlCustomHeaders getCallbackUrlCustomHeaders(){
        return callbackUrlCustomHeaders;
    }

    public void setGatewayUrl(String gatewayUrl){
        this.gatewayUrl = gatewayUrl;
    }

    public String getGatewayUrl(){
        return gatewayUrl;
    }

    public void setPartnerName(String partnerName){
        this.partnerName = partnerName;
    }

    public String getPartnerName(){
        return partnerName;
    }

    public void setSecretKeyTimestamp(String secretKeyTimestamp){
        this.secretKeyTimestamp = secretKeyTimestamp;
    }

    public String getSecretKeyTimestamp(){
        return secretKeyTimestamp;
    }

    public void setDeveloperKey(String developerKey){
        this.developerKey = developerKey;
    }

    public String getDeveloperKey(){
        return developerKey;
    }

    public void setInitiatorLogoUrl(String initiatorLogoUrl){
        this.initiatorLogoUrl = initiatorLogoUrl;
    }

    public String getInitiatorLogoUrl(){
        return initiatorLogoUrl;
    }

    public void setLanguage(String language){
        this.language = language;
    }

    public String getLanguage(){
        return language;
    }

    public void setCallbackUrlCustomParams(CallbackUrlCustomParams callbackUrlCustomParams){
        this.callbackUrlCustomParams = callbackUrlCustomParams;
    }

    public CallbackUrlCustomParams getCallbackUrlCustomParams(){
        return callbackUrlCustomParams;
    }

    @Override
    public String toString(){
        return
                "ParamsResponse{" +
                        "callback_url = '" + callbackUrl + '\'' +
                        ",secret_key = '" + secretKey + '\'' +
                        ",user_code = '" + userCode + '\'' +
                        ",initiator_id = '" + initiatorId + '\'' +
                        ",callback_url_custom_headers = '" + callbackUrlCustomHeaders + '\'' +
                        ",gateway_url = '" + gatewayUrl + '\'' +
                        ",partner_name = '" + partnerName + '\'' +
                        ",secret_key_timestamp = '" + secretKeyTimestamp + '\'' +
                        ",developer_key = '" + developerKey + '\'' +
                        ",initiator_logo_url = '" + initiatorLogoUrl + '\'' +
                        ",language = '" + language + '\'' +
                        ",callback_url_custom_params = '" + callbackUrlCustomParams + '\'' +
                        "}";
    }
}