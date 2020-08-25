package tv.fengmang.xeniadialog.bean;

import java.util.List;

/**
 * Created by Administrator on 2020/2/13 0013.
 */

public class ProvincesBean {

    private String name;
    private List<Province> provinces;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Province> getProvinces() {
        return provinces;
    }

    public void setProvinces(List<Province> provinces) {
        this.provinces = provinces;
    }

    public static class Province {
        /**
         * id : 110000
         * name : 北京
         */

        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Province)) {
                return false;
            }
            Province pn = (Province) o;
            return pn.id.equalsIgnoreCase(this.id) && pn.name.equalsIgnoreCase(this.name);
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + id.hashCode();
            result = 31 * result + name.hashCode();
            return result;
        }
    }
}
