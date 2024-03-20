package astramusfate.wizardry_tales.api.classes;

/** This interface is used for items that should answer can they be activated or not with Conditions **/
public interface IInscribed {

    boolean applyConditions();
    boolean canApplyCondition(String condition);
    boolean applyParameters();
}
