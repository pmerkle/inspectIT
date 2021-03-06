package rocks.inspectit.agent.java.sdk.opentracing.internal.noop;

import java.util.Collections;
import java.util.Map.Entry;

import io.opentracing.NoopSpanContext;
import io.opentracing.NoopTracer;
import io.opentracing.NoopTracerFactory;
import io.opentracing.SpanContext;
import io.opentracing.propagation.Format;
import rocks.inspectit.agent.java.sdk.opentracing.ExtendedTracer;
import rocks.inspectit.agent.java.sdk.opentracing.Timer;
import rocks.inspectit.agent.java.sdk.opentracing.propagation.Propagator;

/**
 * No-operation tracer that implement the {@link ExtendedTracer} interface. Simply delegates calls
 * to the {@link NoopTracer} of the opentracing.io.
 *
 * @author Ivan Senic
 *
 */
public final class NoopExtendedTracerImpl implements ExtendedTracer, NoopTracer {

	/**
	 * Instance for usage.
	 */
	public static final NoopExtendedTracerImpl INSTANCE = new NoopExtendedTracerImpl();

	/**
	 * The noop tracer from the opentracing.io.
	 */
	private static final NoopTracer DEFAULT_NOOP_TRACER = NoopTracerFactory.create();

	/**
	 * The noop context instance to return when calling {@link #getCurrentContext()}.
	 */
	private static final NoopSpanContext NOOP_SPAN_CONTEXT_INSTANCE = new NoopSpanContext() {

		@Override
		public Iterable<Entry<String, String>> baggageItems() {
			return Collections.emptyList();
		}
	};

	/**
	 * Private constructor, use {@link #INSTANCE}.
	 */
	private NoopExtendedTracerImpl() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SpanBuilder buildSpan(String operationName) {
		return DEFAULT_NOOP_TRACER.buildSpan(operationName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <C> void inject(SpanContext spanContext, Format<C> format, C carrier) {
		DEFAULT_NOOP_TRACER.inject(spanContext, format, carrier);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <C> SpanContext extract(Format<C> format, C carrier) {
		return DEFAULT_NOOP_TRACER.extract(format, carrier);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <C> void registerPropagator(Format<C> format, Propagator<C> propagator) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTimer(Timer timer) throws IllegalArgumentException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SpanBuilder buildSpan() {
		return DEFAULT_NOOP_TRACER.buildSpan(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SpanBuilder buildSpan(String operationName, String referenceType, boolean useThreadContext) {
		return DEFAULT_NOOP_TRACER.buildSpan(operationName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SpanContext getCurrentContext() {
		return NOOP_SPAN_CONTEXT_INSTANCE;
	}

}
