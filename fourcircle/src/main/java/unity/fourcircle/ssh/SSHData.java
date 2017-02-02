package unity.fourcircle.ssh;

public class SSHData {

    private String dateString;
    private String timeString;
    private long licenseNumber;

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getTimeString() {
        return timeString;
    }

    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

    public long getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(long licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dateString == null) ? 0 : dateString.hashCode());
        result = prime * result + (int) (licenseNumber ^ (licenseNumber >>> 32));
        result = prime * result + ((timeString == null) ? 0 : timeString.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SSHData other = (SSHData) obj;
        if (dateString == null) {
            if (other.dateString != null) {
                return false;
            }
        } else if (!dateString.equals(other.dateString)) {
            return false;
        }
        if (licenseNumber != other.licenseNumber) {
            return false;
        }
        if (timeString == null) {
            if (other.timeString != null) {
                return false;
            }
        } else if (!timeString.equals(other.timeString)) {
            return false;
        }
        return true;
    }

}
