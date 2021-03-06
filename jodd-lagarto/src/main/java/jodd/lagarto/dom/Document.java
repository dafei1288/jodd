// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import java.util.ArrayList;
import java.util.List;

/**
 * Document node is always a root node.
 * Holds various DOM-related configuration and information.
 */
public class Document extends Node {

	protected long elapsedTime;
	protected final LagartoDomBuilderConfig config;
	protected List<String> errors;

	public Document() {
		this(new LagartoDomBuilderConfig());
	}

	/**
	 * Document constructor with all relevant flags.
	 */
	public Document(LagartoDomBuilderConfig config) {
		super(null, NodeType.DOCUMENT, null);
		this.config = config;
		this.elapsedTime = System.currentTimeMillis();
	}

	@Override
	public Document clone() {
		Document document = cloneTo(new Document(config));
		document.elapsedTime = this.elapsedTime;
		return document;
	}

	/**
	 * Notifies document that parsing is done.
	 */
	protected void end() {
		elapsedTime = System.currentTimeMillis() - elapsedTime;
	}

	@Override
	protected void visitNode(NodeVisitor nodeVisitor) {
		nodeVisitor.document(this);
	}

	// ---------------------------------------------------------------- errors

	/**
	 * Add new error message to the {@link #getErrors() errors list}.
	 * If errors are not collected error, message is ignored.
	 */
	public void addError(String message) {
		if (config.collectErrors) {
			if (errors == null) {
				errors = new ArrayList<String>();
			}
			errors.add(message);
		}
	}

	/**
	 * Returns list of warnings and errors occurred during parsing.
	 * Returns <code>null</code> if parsing was successful; or if
	 * errors are not collected.
	 */
	public List<String> getErrors() {
		return errors;
	}

	// ---------------------------------------------------------------- attr

	/**
	 * Document node does not have attributes.
	 */
	@Override
	public void setAttribute(String name, String value) {
	}

	// ---------------------------------------------------------------- getter

	/**
	 * Returns DOM building elapsed time.
	 */
	public long getElapsedTime() {
		return elapsedTime;
	}

	/**
	 * Returns used {@link jodd.lagarto.dom.LagartoDomBuilderConfig}.
	 */
	public LagartoDomBuilderConfig getConfig() {
		return config;
	}

}