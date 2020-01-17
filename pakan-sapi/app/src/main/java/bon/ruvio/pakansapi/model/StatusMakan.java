package bon.ruvio.pakansapi.model;


public class StatusMakan{

	private int Jam;
	private String Tanggal,Id;
	private boolean Status;
	public StatusMakan(String id, int jam, String tanggal, boolean status){
		Jam = jam;
		Id = id;
		Tanggal = tanggal;
		Status = status;

	}
	public void setTanggal(String tanggal) {
		Tanggal = tanggal;
	}

	public boolean isStatus() {
		return Status;
	}

	public void setStatus(boolean status) {
		Status = status;
	}



	public void setJam(int jam){
		Jam = jam;
	}

	public int getJam(){
		return Jam;
	}

	public String getTanggal() {
		return Tanggal;
	}

}