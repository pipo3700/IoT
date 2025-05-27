package componentes;

public class SmartCar {

    protected String brokerURL = null;

    protected String smartCarID = null;
    protected RoadPlace rp = null; // simula la ubicación actual del vehículo
    protected SmartCar_RoadInfoSubscriber subscriber = null;
    protected SmartCar_InicidentNotifier notifier = null;

    public SmartCar(String id, String brokerURL) {
        this.setSmartCarID(id);
        this.brokerURL = brokerURL;

        this.setCurrentRoadPlace(new RoadPlace("R6s1", 0));

        this.notifier = new SmartCar_InicidentNotifier(id + ".incident-notifier", this, this.brokerURL);
        this.notifier.connect();
    }

    public void setSmartCarID(String smartCarID) {
        this.smartCarID = smartCarID;
    }

    public String getSmartCarID() {
        return smartCarID;
    }

    public void setCurrentRoadPlace(RoadPlace rp) {
        this.rp = rp;

        // Si ya hay un suscriptor, lo desconectamos
        if (this.subscriber != null) {
            this.subscriber.disconnect();
        }

        // Crear nuevo suscriptor para este tramo
        this.subscriber = new SmartCar_RoadInfoSubscriber(this.smartCarID + ".road-subscriber", this, this.brokerURL);
        this.subscriber.connect();
    }

    public RoadPlace getCurrentPlace() {
        return rp;
    }

    public void changeKm(int km) {
        this.getCurrentPlace().setKm(km);
    }

    public void getIntoRoad(String road, int km) {
        this.getCurrentPlace().setRoad(road);
        this.getCurrentPlace().setKm(km);
    }

    public void notifyIncident(String incidentType) {
        if (this.notifier == null)
            return;

        this.notifier.alert(this.getSmartCarID(), incidentType, this.getCurrentPlace());
    }

    // Publicar que entra en un segmento
    public void publishVehicleIn() {
        String topic = "es/upv/pros/tatami/smartcities/traffic/PTPaterna/road/" + this.rp.getRoad() + "/traffic";

        String payload = "{\n" +
            "  \"action\":\"VEHICLE_IN\",\n" +
            "  \"road\":\"" + this.rp.getRoad().substring(0, 2) + "\",\n" +
            "  \"road-segment\":\"" + this.rp.getRoad() + "\",\n" +
            "  \"vehicle-id\":\"" + this.smartCarID + "\",\n" +
            "  \"position\":" + this.rp.getKm() + ",\n" +
            "  \"role\":\"PrivateUsage\"\n" +
            "}";

        this.notifier.publish(topic, payload);
    }

    // Publicar que sale del segmento
    public void publishVehicleOut() {
        String topic = "es/upv/pros/tatami/smartcities/traffic/PTPaterna/road/" + this.rp.getRoad() + "/traffic";

        String payload = "{\n" +
            "  \"action\":\"VEHICLE_OUT\",\n" +
            "  \"road\":\"" + this.rp.getRoad().substring(0, 2) + "\",\n" +
            "  \"road-segment\":\"" + this.rp.getRoad() + "\",\n" +
            "  \"vehicle-id\":\"" + this.smartCarID + "\",\n" +
            "  \"position\":" + this.rp.getKm() + ",\n" +
            "  \"role\":\"PrivateUsage\"\n" +
            "}";

        this.notifier.publish(topic, payload);
    }
}
