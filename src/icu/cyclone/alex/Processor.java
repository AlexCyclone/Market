package icu.cyclone.alex;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Processor {

	private static final String U = "u";
	private static final String Q = "q";
	private static final String O = "o";

	private static final String BID = "bid";
	private static final String ASK = "ask";

	private static final String BEST_BID = "best_bid";
	private static final String BEST_ASK = "best_ask";
	private static final String SIZE = "size";

	private static final String BUY = "buy";
	private static final String SELL = "sell";

	private static final String EMPTY = "";
	private static final String COMMA = ",";


	private Market market;
	private String requestFile;
	private String responseFile;
	private List<String> out = new ArrayList<>();

	public Processor(String requestFile, String responseFile, Market market) {
		this.requestFile = requestFile;
		this.responseFile = responseFile;
		this.market = market;
	}

	public void process() {
		try {
			List<String> commands = read(requestFile);
			for (String command : commands) {
				runCommand(command);
			}

			write(responseFile, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void runCommand(String command) {
		String[] params = command.split(COMMA);
		if (params.length < 2) {
			return;
		} else if (U.equals(params[0])) {
			update(params);
		} else if (Q.equals(params[0])) {
			query(params);
		} else if (O.equals(params[0])) {
			order(params);
		}
	}

	private void update(String[] params) {
		if (params.length == 4) {
			try {
				if (ASK.equals(params[3])) {
					market.addAsk(Integer.parseInt(params[1]), Integer.parseInt(params[2]));
				} else if (BID.equals(params[3])) {
					market.addBid(Integer.parseInt(params[1]), Integer.parseInt(params[2]));
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
	}

	private void query(String[] params) {
		String response = EMPTY;
		if (params.length >= 2) {
			if (BEST_BID.equals(params[1])) {
				Map.Entry<Integer, Integer> bid = market.getBestBid();
				response = bid != null ? bid.getKey() + COMMA + bid.getValue() : EMPTY;
			} else if (BEST_ASK.equals(params[1])) {
				Map.Entry<Integer, Integer> ask = market.getBestAsk();
				response = ask != null ? ask.getKey() + COMMA + ask.getValue() : EMPTY;
			} else if (SIZE.equals(params[1]) && params.length == 3) {
				try {
					response = String.valueOf(market.getSize(Integer.parseInt(params[2])));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			out.add(response);
		}
	}

	private void order(String[] params) {
		if (params.length == 3) {
			try {
				if (BUY.equals(params[1])) {
					market.buy(Integer.parseInt(params[2]));
				} else if (SELL.equals(params[1])) {
					market.sell(Integer.parseInt(params[2]));
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
	}

	private List<String> read(String filepath) throws IOException {
		Path path = Paths.get(filepath);
		List<String> lines;
		lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		return lines;
	}


	private void write(String filepath, List<String> data) throws IOException {
		Path path = Paths.get(filepath);
		Files.write(path, data, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
	}
}
