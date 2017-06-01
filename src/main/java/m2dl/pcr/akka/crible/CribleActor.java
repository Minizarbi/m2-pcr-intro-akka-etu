package m2dl.pcr.akka.crible;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;

public class CribleActor extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private ActorRef next;
    private int premier;

    public CribleActor(int premier) {
        log.info("Premier : " + premier);
        this.premier = premier;
    }

    private Procedure<Object> last = new Procedure<Object>() {
        public void apply(Object msg) throws Exception {
            if (msg instanceof Integer) {
                int x = (Integer) msg;
                if (x % premier != 0) {
                    next = getContext().actorOf(Props.create(CribleActor.class, x));
                    getContext().become(inter);
                }
            }
        }
    };

    private Procedure<Object> inter = new Procedure<Object>() {
        public void apply(Object msg) throws Exception {
            if (msg instanceof Integer) {
                int x = (Integer) msg;
                if (x % premier != 0) {
                    next.tell(msg, getSelf());
                }
            }
        }
    };

    public void onReceive(Object o) throws Exception {
        last.apply(o);
    }
}
