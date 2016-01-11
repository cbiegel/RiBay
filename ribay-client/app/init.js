/**
 * Created by Christian on 07.01.2016.
 */

$.post("//localhost:8080/keepSessionAlive", function (data, status) {
    alert("Data: " + data + "\nStatus: " + status);
});