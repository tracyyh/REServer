package sales;

// Simple class to provide test data in SalesDAO

public class HomeSale {
  
   private int property_id;
   private String download_date;
   private String council_name;
   private int purchase_price;
   private String address;
   private int post_code;
   private String property_type;
   private String strata_lot_number;
   private String property_name;
   private int area;
   private String area_type;
   private String contract_date;
   private String settlement_date;
   private String zoning;
   private String nature_of_property;
   private String primary_purpose;
   private String legal_description;




   public HomeSale(int property_id, String download_date, String council_name, int purchase_price, String address, int post_code,
    String property_type, String strata_lot_number, String property_name, int area, String area_type, String contract_date, 
    String settlement_date, String zoning, String nature_of_property, String primary_purpose, String legal_description) {
       this.property_id = property_id;
       this.download_date = download_date;
       this.council_name = council_name;       
       this.purchase_price = purchase_price;
       this.address = address;
       this.post_code = post_code;
       this.property_type = property_type;
       this.strata_lot_number = strata_lot_number;
       this.property_name = property_name;
       this.area = area;
       this.area_type = area_type;
       this.contract_date = contract_date;
       this.settlement_date = settlement_date;
       this.zoning = zoning;
       this.nature_of_property = nature_of_property;
       this.primary_purpose = primary_purpose;
       this.legal_description = legal_description;
   }

public int getProperty_id() {
    return property_id;
}

public void setProperty_id(int property_id) {
    this.property_id = property_id;
}

public String getDownload_date() {
    return download_date;
}

public void setDownload_date(String download_date) {
    this.download_date = download_date;
}

public String getCouncil_name() {
    return council_name;
}

public void setCouncil_name(String council_name) {
    this.council_name = council_name;
}

public int getPurchase_price() {
    return purchase_price;
}

public void setPurchase_price(int purchase_price) {
    this.purchase_price = purchase_price;
}

public String getAddress() {
    return address;
}

public void setAddress(String address) {
    this.address = address;
}

public int getPost_code() {
    return post_code;
}

public void setPost_code(int post_code) {
    this.post_code = post_code;
}

public String getProperty_type() {
    return property_type;
}

public void setProperty_type(String property_type) {
    this.property_type = property_type;
}

public String getStrata_lot_number() {
    return strata_lot_number;
}

public void setStrata_lot_number(String strata_lot_number) {
    this.strata_lot_number = strata_lot_number;
}

public String getProperty_name() {
    return property_name;
}

public void setProperty_name(String property_name) {
    this.property_name = property_name;
}

public int getArea() {
    return area;
}

public void setArea(int area) {
    this.area = area;
}

public String getArea_type() {
    return area_type;
}

public void setArea_type(String area_type) {
    this.area_type = area_type;
}

public String getContract_date() {
    return contract_date;
}

public void setContract_date(String contract_date) {
    this.contract_date = contract_date;
}

public String getSettlement_date() {
    return settlement_date;
}

public void setSettlement_date(String settlement_date) {
    this.settlement_date = settlement_date;
}

public String getZoning() {
    return zoning;
}

public void setZoning(String zoning) {
    this.zoning = zoning;
}

public String getNature_of_property() {
    return nature_of_property;
}

public void setNature_of_property(String nature_of_property) {
    this.nature_of_property = nature_of_property;
}

public String getPrimary_purpose() {
    return primary_purpose;
}

public void setPrimary_purpose(String primary_purpose) {
    this.primary_purpose = primary_purpose;
}

public String getLegal_description() {
    return legal_description;
}

public void setLegal_description(String legal_description) {
    this.legal_description = legal_description;
}
}