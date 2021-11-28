import "whatwg-fetch";

export enum Method {
  GET = "GET",
  POST = "POST",
  PUT = "PUT",
  DELETE = "DELETE",
}

function parseQuery<T>(data: T) {
  let query = "";
  if (data) {
    const datas = [];
    for (const k in data) {
      if (k) {
        datas.push(`${k}=${data[k]}`);
      }
    }
    if (datas.length > 0) {
      query = "?" + datas.join("&");
    }
  }
  return query;
}

function init<T>(method: Method, data: T) {
  if (method === Method.GET) {
    return {};
  }
  return {
    method,
    body: JSON.stringify(data),
    headers: {
      "Content-Type": "application/json; charset=utf-8"
    }
  }
}

export async function requestJSON<T, S>(url: string, method: Method, data: T): Promise<S> {
  let query = "";
  if (method === Method.GET) {
    query = parseQuery<T>(data);
  }
  const res = await fetch(url + query, init(method, data));
  if (res.ok) {
    return res.json();
  }
  throw new Error(res.statusText);
}

export async function requestString<T>(url: string, method: Method, data: T): Promise<string> {
  let query = "";
  if (method === Method.GET) {
    query = parseQuery(data);
  }
  const res = await fetch(url + query, init(method, data));
  if (res.ok) {
    return res.text();
  }
  throw new Error(res.statusText);
}
