

module CompanyBrain {
    requires java.desktop;
    requires java.sql;
	requires junit;
    requires com.formdev.flatlaf;

    opens com.companybrain.test to junit;
	
	
}