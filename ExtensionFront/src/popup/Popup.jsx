import React, { useEffect, useState } from "react";

const Popup = () => {
  const [url, setUrl] = useState("");
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    // Get active tab URL
    chrome.tabs.query({ active: true, currentWindow: true }, async (tabs) => {
      const currentUrl = tabs[0].url;
      setUrl(currentUrl);

      try {

        // HARDCODED RESPONSE

        const response = await fetch("http://localhost:8080/api/analyze", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ url: currentUrl }),
        });

        // RESPONSE FOR THE AI INTEGRATED
        // const response = await fetch("http://localhost:8080/api/security/analyze", {
        //   method: "POST",
        //   headers: { "Content-Type": "application/json" },
        //   body: JSON.stringify(currentUrl), // ✅ NEW
        // });

        if (!response.ok) throw new Error("Failed to fetch");

        const result = await response.json();
        setReport(result);
      } catch (err) {
        console.error(err);
        setError("Could not fetch analysis.");
      } finally {
        setLoading(false);
      }
    });
  }, []);

  return (
    <div className="p-4 w-80 bg-white text-gray-900 font-sans">
      <h1 className="text-xl font-bold mb-2">Website Security Report</h1>
      <p className="text-sm text-gray-600 mb-3 break-words">{url}</p>

      {loading ? (
        <p className="text-blue-500">Analyzing...</p>
      ) : error ? (
        <p className="text-red-500">{error}</p>
      ) : (
        <div className="space-y-2">
          <div className="flex items-center justify-between">
            <span>🔒 HTTPS</span>
            <span>{report.https ? "✅" : "❌"}</span>
          </div>

          <div className="flex items-center justify-between">
            <span>🛡️ CSP Header</span>
            <span>{report.hasCsp ? "✅" : "❌"}</span>
          </div>

          <div className="mt-3">
            <h2 className="font-semibold">Score: {report.score}/100</h2>
            {report.warnings?.length > 0 && (
              <ul className="list-disc ml-4 text-sm mt-1 text-yellow-600">
                {report.warnings.map((w, idx) => (
                  <li key={idx}>{w}</li>
                ))}
              </ul>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default Popup;
