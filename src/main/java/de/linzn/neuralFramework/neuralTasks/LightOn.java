package de.linzn.neuralFramework.neuralTasks;

import de.linzn.homeDevices.HomeDevicesPlugin;
import de.linzn.homeDevices.devices.switches.SwitchableMQTTDevice;
import de.linzn.neuralFramework.neuralStructure.NeuralTask;
import de.linzn.neuralFramework.neuralStructure.objects.NeuralCombination;
import de.linzn.neuralFramework.neuralStructure.objects.NeuralLocation;
import de.linzn.neuralFramework.neuralStructure.objects.NeuralObject;
import org.json.JSONObject;

public class LightOn implements NeuralTask {

    private final JSONObject jsonObject = new JSONObject();
    private boolean wasSuccess = false;

    @Override
    public void runTask(NeuralObject neuralObject, NeuralCombination neuralCombination, NeuralLocation neuralLocation, JSONObject otherInput) {
        if(otherInput.has("data")) {
            String deviceConfigName = otherInput.getJSONObject("data").getString("device_name");

            SwitchableMQTTDevice switchableMQTTDevice = HomeDevicesPlugin.homeDevicesPlugin.getSwitchableMQTTDevice(deviceConfigName);
            switchableMQTTDevice.switchDevice(true);
            jsonObject.put("text", "Device switched to status " + true);
            jsonObject.put("success", true);
            this.wasSuccess = true;
        } else {
            jsonObject.put("success", false);
        }
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
        return 100001L;
    }
}
