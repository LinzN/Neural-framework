package de.linzn.neuralFramework.neuralTasks;

import de.linzn.homeDevices.HomeDevicesPlugin;
import de.linzn.homeDevices.devices.TasmotaMQTTDevice;
import de.linzn.neuralFramework.neuralStructure.NeuralTask;
import de.linzn.neuralFramework.neuralStructure.objects.NeuralCombination;
import de.linzn.neuralFramework.neuralStructure.objects.NeuralLocation;
import de.linzn.neuralFramework.neuralStructure.objects.NeuralObject;
import org.json.JSONObject;

public class LightOff implements NeuralTask {

    private final JSONObject jsonObject = new JSONObject();
    private boolean wasSuccess = false;

    @Override
    public void runTask(NeuralObject neuralObject, NeuralCombination neuralCombination, NeuralLocation neuralLocation, JSONObject otherInput) {
        TasmotaMQTTDevice tasmotaMQTTDevice = HomeDevicesPlugin.homeDevicesPlugin.getTasmotaDevice("ambiente");
        tasmotaMQTTDevice.switchDevice(false);
        jsonObject.put("text", "Device switched to status " + false);
        jsonObject.put("success", true);
        this.wasSuccess = true;
    }

    @Override
    public JSONObject taskCompleteData() {
        return jsonObject;
    }

    @Override
    public boolean wasSuccess() {
        return wasSuccess;
    }

    @Override
    public long GET_TASK_ID() {
        return 100002L;
    }
}
