package com.xk.player.ole;

public interface IOleComponent {

	public void invokeOleFunction( String[] functionPath, Object[] args );

	public Object invokeOleFunctionWithResult( String[] functionPath,
			Object[] args );

	public boolean setOleProperty( String[] propertyPath, Object[] args );

	public Object getOleProperty( String[] propertyPath, Object[] args );
}
