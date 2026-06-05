

module CompanyBrain {
    requires java.desktop;
    requires java.sql;
	requires junit;

	opens com.companybrain.test to junit;
	
	
}