package works.hop.javro.gen.metadata;

import works.hop.javro.gen.core.Node;

import java.util.List;

public interface OnReadyListener {

    void completed(List<Node> readyList);
}
