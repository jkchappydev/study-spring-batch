package io.springbatch.springbatchlecture.configuration;

import org.springframework.batch.item.*;

import java.util.List;

public class CustomItemStreamReader implements ItemStreamReader<String> {

    private final List<String> items;
    private int index = -1;
    private boolean restart = false;

    public CustomItemStreamReader(List<String> items) {
        this.items = items;
        this.index = 0;
    }

    @Override
    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        String item = null;

        // 아직 읽을 데이터가 남아있으면 하나 꺼내고 index 증가
        if (this.index < this.items.size()) {
            item = this.items.get(index);
            index++;
        }

        // ex) 6번째까지만 읽고(= index 가 6이 되는 시점) 최초 실행이면 일부러 실패시켜 재시작 유도
        if (this.index == 6 && !restart) {
            throw new RuntimeException("Restart is required");
        }

        return item;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        if (executionContext.containsKey("index")) { // 재시작일 경우, index 라는 Key 가 executionContext 에 있다면 (=DB에 저장되어 있다면) 해당 index 부터 이어서 시작
            index = executionContext.getInt("index");
            this.restart = true;
        } else { // 최초 실행이면 index=0으로 시작하고, key 를 미리 만들어둠
            index = 0;
            executionContext.put("index", index);
        }
    }

    // 데이터가 총 10 개고, Chunk Size 를 5 로 설정함 -> update() 는 두번 호출될것임.
    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        // 현재 index 를 ExecutionContext 에 계속 저장 -> 실패 시 이 지점부터 재시작 가능
        executionContext.put("index", index);
    }

    @Override
    public void close() throws ItemStreamException {
        System.out.println("customItemStreamReader close");
    }

}
