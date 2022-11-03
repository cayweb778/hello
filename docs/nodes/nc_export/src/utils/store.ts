// @ts-nocheck
export declare interface BoozsoftDefineStore<S, A, Se> {
    /**
     * Unique string key to identify the store across the application.
     */
    /**
     * Function to create a fresh state.
     */
    state?: ()=>S;
    /**
     * Optional object of getters.
     */
    /**
     * Optional object of actions.
     */
    actions?:{};
    setup: Se;
}
export declare interface StoreDefinition<S,A,Se> {
    /**
     * Returns a store, creates it if necessary.
     *
     * @param pinia - Pinia instance to retrieve the store
     */
    S,
    A,
    Se
}
export function defineStore<S,A,Se>(params:{state:S,actions:A,setup:Se}):S & A & Se{
    const {state,actions,setup}=params
    return {...state,...actions}
}
