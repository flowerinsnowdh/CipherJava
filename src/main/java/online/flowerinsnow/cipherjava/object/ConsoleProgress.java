package online.flowerinsnow.cipherjava.object;

public class ConsoleProgress {
    private static final Object CONSOLE_LOCK = new Object();

    private long current;
    private final long max;
    private boolean drawedBefore;
    private int lastDrawed = -1;

    public ConsoleProgress(long max) {
        this.max = Math.max(max, 1);
    }

    public long getMax() {
        return max;
    }

    public synchronized long getCurrent() {
        return current;
    }

    public synchronized void setCurrent(long current) {
        this.current = Math.max(Math.min(current, max), 0);
        this.update();
//        System.out.println("this.current = " + this.current);
    }

    // 100% <====================>
    private void update() {
        int progress = getPercentage();
        if (progress == lastDrawed) {
            return;
        }
        lastDrawed = progress;
        if (this.drawedBefore) {
            this.clean();
        }
        this.render();
        this.drawedBefore = true;
    }

    private void clean() {
        synchronized (CONSOLE_LOCK) {
            System.out.print("\b".repeat(27) /* + " ".repeat(27) + "\b".repeat(27) */);
        }
    }

    private void render() {
        int progress = getPercentage();
        int in20 = Math.min((int) (((double) current / (double) max) * 20.0), 20);
        synchronized (CONSOLE_LOCK) {
            if (progress < 10) {
                System.out.print("  " + progress);
            } else if (progress < 100) {
                System.out.print(" " + progress);
            } else {
                System.out.print(progress);
            }
            System.out.print("% <" + "=".repeat(in20) + " ".repeat(20 - in20) + ">");
        }
    }

    private synchronized int getPercentage() {
        return Math.min((int) (((double) current / (double) max) * 100.0), 100);
    }
}
