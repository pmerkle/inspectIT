package rocks.inspectit.server.alerting.action;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Iterator;
import java.util.List;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.testng.annotations.Test;

import rocks.inspectit.server.alerting.AlertRegistry;
import rocks.inspectit.server.alerting.state.AlertingState;
import rocks.inspectit.shared.all.testbase.TestBase;
import rocks.inspectit.shared.cs.ci.AlertingDefinition;
import rocks.inspectit.shared.cs.ci.AlertingDefinition.ThresholdType;
import rocks.inspectit.shared.cs.communication.data.cmr.Alert;
import rocks.inspectit.shared.cs.communication.data.cmr.AlertClosingReason;

/**
 * Tests the {@link AlertingActionService}.
 *
 * @author Alexander Wert
 * @author Marius Oehler
 *
 */
@SuppressWarnings("PMD")
public class AlertingActionServiceTest extends TestBase {

	@InjectMocks
	AlertingActionService alertingService;

	@Mock
	Logger log;

	@Mock
	AlertRegistry alertRegistry;

	@Mock
	List<IAlertAction> alertActions;

	/**
	 * Tesets the
	 * {@link AlertingActionService#alertStarting(rocks.inspectit.server.alerting.state.AlertingState, double)}
	 * method.
	 *
	 * @author Alexander Wert
	 *
	 */
	public static class AlertStarting extends AlertingActionServiceTest {

		@Test
		@SuppressWarnings("unchecked")
		public void startAlert() {
			IAlertAction alertAction = Mockito.mock(IAlertAction.class);
			Iterator<IAlertAction> iterator = Mockito.mock(Iterator.class);
			when(iterator.hasNext()).thenReturn(true, false);
			when(iterator.next()).thenReturn(alertAction);
			when(alertActions.iterator()).thenReturn(iterator);
			AlertingState alertingState = Mockito.mock(AlertingState.class);
			AlertingDefinition alertingDefinition = Mockito.mock(AlertingDefinition.class);
			when(alertingState.getAlertingDefinition()).thenReturn(alertingDefinition);
			when(alertingState.getLastCheckTime()).thenReturn(1234L);

			alertingService.alertStarting(alertingState, 1.0D);

			ArgumentCaptor<Alert> captor = ArgumentCaptor.forClass(Alert.class);
			verify(alertRegistry).registerAlert(captor.capture());
			verifyNoMoreInteractions(alertRegistry);
			assertThat(captor.getValue().getAlertingDefinition(), is(alertingDefinition));
			assertThat(captor.getValue().getStartTimestamp(), is(1234L));
			verify(alertAction).onStarting(alertingState);
			verify(alertActions).iterator();
			verifyNoMoreInteractions(alertActions);
			verify(alertingState).getAlertingDefinition();
			verify(alertingState).getLastCheckTime();
			verify(alertingState).setAlert(any(Alert.class));
			verify(alertingState).setExtremeValue(1.0D);
			verifyNoMoreInteractions(alertingState);
		}

		@Test(expectedExceptions = IllegalArgumentException.class)
		public void alertingStateIsNull() {
			alertingService.alertStarting(null, 1.0D);
		}
	}

	/**
	 * Tests the
	 * {@link AlertingActionService#alertOngoing(rocks.inspectit.server.alerting.state.AlertingState)}
	 * method.
	 *
	 * @author Alexander Wert
	 *
	 */
	public static class AlertOngoing extends AlertingActionServiceTest {

		@Test
		@SuppressWarnings("unchecked")
		public void upperThresholdNewExtremeValue() {
			IAlertAction alertAction = Mockito.mock(IAlertAction.class);
			Iterator<IAlertAction> iterator = Mockito.mock(Iterator.class);
			when(iterator.hasNext()).thenReturn(true, false);
			when(iterator.next()).thenReturn(alertAction);
			when(alertActions.iterator()).thenReturn(iterator);
			AlertingDefinition alertingDefinition = Mockito.mock(AlertingDefinition.class);
			when(alertingDefinition.getThresholdType()).thenReturn(ThresholdType.UPPER_THRESHOLD);
			AlertingState alertingState = Mockito.mock(AlertingState.class);
			when(alertingState.getAlertingDefinition()).thenReturn(alertingDefinition);
			when(alertingState.getLastCheckTime()).thenReturn(1234L);
			when(alertingState.getExtremeValue()).thenReturn(5D);

			alertingService.alertOngoing(alertingState, 10D);

			verify(alertingState).setExtremeValue(10D);
			verify(alertingState).getExtremeValue();
			verify(alertingState).getAlertingDefinition();
			verifyNoMoreInteractions(alertingState);
			verify(alertAction).onOngoing(alertingState);
			verifyNoMoreInteractions(alertAction);
			verify(alertActions).iterator();
			verifyNoMoreInteractions(alertActions);
			verifyZeroInteractions(alertRegistry);
		}

		@Test
		@SuppressWarnings("unchecked")
		public void upperThreshold() {
			IAlertAction alertAction = Mockito.mock(IAlertAction.class);
			Iterator<IAlertAction> iterator = Mockito.mock(Iterator.class);
			when(iterator.hasNext()).thenReturn(true, false);
			when(iterator.next()).thenReturn(alertAction);
			when(alertActions.iterator()).thenReturn(iterator);
			AlertingDefinition alertingDefinition = Mockito.mock(AlertingDefinition.class);
			when(alertingDefinition.getThresholdType()).thenReturn(ThresholdType.UPPER_THRESHOLD);
			AlertingState alertingState = Mockito.mock(AlertingState.class);
			when(alertingState.getAlertingDefinition()).thenReturn(alertingDefinition);
			when(alertingState.getLastCheckTime()).thenReturn(1234L);
			when(alertingState.getExtremeValue()).thenReturn(5D);

			alertingService.alertOngoing(alertingState, 3D);

			verify(alertingState).setExtremeValue(5D);
			verify(alertingState).getExtremeValue();
			verify(alertingState).getAlertingDefinition();
			verifyNoMoreInteractions(alertingState);
			verify(alertAction).onOngoing(alertingState);
			verifyNoMoreInteractions(alertAction);
			verify(alertActions).iterator();
			verifyNoMoreInteractions(alertActions);
			verifyZeroInteractions(alertRegistry);
		}

		@Test
		@SuppressWarnings("unchecked")
		public void lowerThresholdNewExtremeValue() {
			IAlertAction alertAction = Mockito.mock(IAlertAction.class);
			Iterator<IAlertAction> iterator = Mockito.mock(Iterator.class);
			when(iterator.hasNext()).thenReturn(true, false);
			when(iterator.next()).thenReturn(alertAction);
			when(alertActions.iterator()).thenReturn(iterator);
			AlertingDefinition alertingDefinition = Mockito.mock(AlertingDefinition.class);
			when(alertingDefinition.getThresholdType()).thenReturn(ThresholdType.LOWER_THRESHOLD);
			AlertingState alertingState = Mockito.mock(AlertingState.class);
			when(alertingState.getAlertingDefinition()).thenReturn(alertingDefinition);
			when(alertingState.getLastCheckTime()).thenReturn(1234L);
			when(alertingState.getExtremeValue()).thenReturn(5D);

			alertingService.alertOngoing(alertingState, 3D);

			verify(alertingState).setExtremeValue(3D);
			verify(alertingState).getExtremeValue();
			verify(alertingState).getAlertingDefinition();
			verifyNoMoreInteractions(alertingState);
			verify(alertAction).onOngoing(alertingState);
			verifyNoMoreInteractions(alertAction);
			verify(alertActions).iterator();
			verifyNoMoreInteractions(alertActions);
			verifyZeroInteractions(alertRegistry);
		}

		@Test
		@SuppressWarnings("unchecked")
		public void lowerThreshold() {
			IAlertAction alertAction = Mockito.mock(IAlertAction.class);
			Iterator<IAlertAction> iterator = Mockito.mock(Iterator.class);
			when(iterator.hasNext()).thenReturn(true, false);
			when(iterator.next()).thenReturn(alertAction);
			when(alertActions.iterator()).thenReturn(iterator);
			AlertingDefinition alertingDefinition = Mockito.mock(AlertingDefinition.class);
			when(alertingDefinition.getThresholdType()).thenReturn(ThresholdType.LOWER_THRESHOLD);
			AlertingState alertingState = Mockito.mock(AlertingState.class);
			when(alertingState.getAlertingDefinition()).thenReturn(alertingDefinition);
			when(alertingState.getLastCheckTime()).thenReturn(1234L);
			when(alertingState.getExtremeValue()).thenReturn(5D);

			alertingService.alertOngoing(alertingState, 10D);

			verify(alertingState).setExtremeValue(5D);
			verify(alertingState).getExtremeValue();
			verify(alertingState).getAlertingDefinition();
			verifyNoMoreInteractions(alertingState);
			verify(alertAction).onOngoing(alertingState);
			verifyNoMoreInteractions(alertAction);
			verify(alertActions).iterator();
			verifyNoMoreInteractions(alertActions);
			verifyZeroInteractions(alertRegistry);
		}

		@Test(expectedExceptions = IllegalArgumentException.class)
		public void alertingStateIsNull() {
			alertingService.alertOngoing(null, 1.0D);
		}
	}

	/**
	 * Tesets the
	 * {@link AlertingActionService#alertEnding(rocks.inspectit.server.alerting.state.AlertingState)}
	 * method.
	 *
	 * @author Alexander Wert
	 *
	 */
	public static class AlertEnding extends AlertingActionServiceTest {

		@Test
		@SuppressWarnings("unchecked")
		public void alertEnded() {
			IAlertAction alertAction = Mockito.mock(IAlertAction.class);
			Iterator<IAlertAction> iterator = Mockito.mock(Iterator.class);
			when(iterator.hasNext()).thenReturn(true, false);
			when(iterator.next()).thenReturn(alertAction);
			when(alertActions.iterator()).thenReturn(iterator);
			Alert alert = Mockito.mock(Alert.class);
			AlertingState alertingState = Mockito.mock(AlertingState.class);
			when(alertingState.getAlert()).thenReturn(alert);

			long leftBorder = System.currentTimeMillis();
			alertingService.alertEnding(alertingState);
			long rightBorder = System.currentTimeMillis();

			ArgumentCaptor<Long> timeCaptor = ArgumentCaptor.forClass(Long.class);
			ArgumentCaptor<AlertClosingReason> closingReasonCaptor = ArgumentCaptor.forClass(AlertClosingReason.class);
			verify(alert).close(timeCaptor.capture(), closingReasonCaptor.capture());
			assertThat(timeCaptor.getValue(), greaterThanOrEqualTo(leftBorder));
			assertThat(timeCaptor.getValue(), lessThanOrEqualTo(rightBorder));
			assertThat(closingReasonCaptor.getValue(), is(AlertClosingReason.ALERT_RESOLVED));
			verifyNoMoreInteractions(alert);
			verify(alertingState).getAlert();
			verify(alertingState).setAlert(null);
			verifyNoMoreInteractions(alertingState);
			verify(alertAction).onEnding(alertingState);
			verifyNoMoreInteractions(alertAction);
			verify(alertActions).iterator();
			verifyNoMoreInteractions(alertActions);
			verifyZeroInteractions(alertRegistry);
		}

		@Test(expectedExceptions = IllegalArgumentException.class)
		public void alertingStateIsNull() {
			alertingService.alertEnding(null);
		}
	}
}
