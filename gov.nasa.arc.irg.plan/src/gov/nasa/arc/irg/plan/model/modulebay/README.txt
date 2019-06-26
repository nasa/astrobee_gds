Yes, most of these classes might make more sense in gov.nasa.arc.irg.plan.freeflyer 
instead of in this plugin.  However, the JsonSubTypes annotation must be put on 
the higher classes in the hierarchy, and the subtypes have to be in this plugin
because this plugin can't depend on a lower one without causing a circular
dependency.  So these classes are all getting stuck into this enormous plugin.