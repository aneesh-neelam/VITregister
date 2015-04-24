package app.vit.vitregister.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServerResponse {

    @Expose
    @SerializedName("result")
    private Result result;

    public ServerResponse(Result result) {
        this.result = result;
    }

    public Result getResult() {
        return result;
    }

}
